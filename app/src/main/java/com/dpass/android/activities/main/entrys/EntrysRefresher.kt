package com.dpass.android.activities.main.entrys

import android.content.ContentValues
import android.content.Context
import com.dpass.android.R
import com.dpass.android.bean.*
import com.dpass.android.common.BroadCastConstants
import com.dpass.android.common.CONTRACT_ADDRESS
import com.dpass.android.common.GAS_LIMIT
import com.dpass.android.live.AccountStateLiveData
import com.dpass.android.net.AccountRequest
import com.dpass.android.net.NetWorker
import com.dpass.android.net.Response
import com.dpass.android.stroage.MyDatabaseOpenHelper
import com.dpass.android.stroage.database
import com.dpass.android.utils.Logger
import com.google.gson.GsonBuilder
import io.nebulas.crypto.cipher.Cipher
import io.nebulas.crypto.keystore.Algorithm
import io.nebulas.nebulas.Nebulas
import io.nebulas.util.ByteUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import org.json.JSONObject


fun EntrysFragment.refreshData(privateKeyRaw: ByteArray, database: MyDatabaseOpenHelper, passContext: Context){
    if (isRefreshing) {
        return
    }
    isRefreshing = true
    refreshLayout?.isRefreshing = true
    var params = ""
    try {
        params = GsonBuilder().create().toJson(AccountRequest(Nebulas.getMyWalletAddress()))
    }catch (e: Exception){
        Logger.i(e.toString())
    }
    val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params)
    Observable.zip(
            NetWorker.callRxApiLauncher().gasPrice(),
            NetWorker.callRxApiLauncher().accountState(requestBody),
            BiFunction<Response<GasPrice>, Response<AccountState>, Pair<GasPrice, AccountState>> { t1, t2 ->
                Pair(t1.result, t2.result)
            })
            .concatMap {
                val nonce = it.second.nonce
                it.second.nonce ++
                AccountStateLiveData.get().postValue(it.second)
                val myAddress = Nebulas.getMyWalletAddress()
                val contractAddress = CONTRACT_ADDRESS

                val request = CallRequest()
                request.from = myAddress
                request.to   = contractAddress
                request.nonce = nonce.toLong() + 1
                request.gasLimit = GAS_LIMIT
                request.gasPrice = it.first.gasPrice
                request.value = "0"

                val requestContract = CallRequest.ContractBean()
                requestContract.function = "get"
                requestContract.args     = ""

                request.contract = requestContract

                val requestParams = GsonBuilder().create().toJson(request)

                val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestParams)
                NetWorker.callRxApiLauncher().call(requestBody)
            }.concatMap {
                try {
                    val resultJson = JSONObject(it.result.result)
                    val gson = GsonBuilder().create()
                    val cipher = Cipher(Algorithm.SCRYPT)

                    val rowParser = classParser<EntryStore>()
                    val entryStores = passContext.database.readableDatabase
                            .select(EntryStore.tableName, EntryStore.IDColumn, EntryStore.valueColumn,
                                    EntryStore.stateColumn, EntryStore.txHashColumn, EntryStore.nameColumn,
                                    EntryStore.usernameColumn, EntryStore.hashIdColumn).exec {
                                parseList(rowParser)
                            }

                    resultJson.keys().forEach {
                        val hashId = it
                        val findExist = entryStores.find{ it.hashId == hashId}
                        if (findExist == null){
                            Logger.i("save new entry hashId=$hashId")
                            val cipherByte = ByteUtils.Base64ToBytes(resultJson[it].toString())
                            val plainText = cipher.decrypt(cipherByte, privateKeyRaw)
                            val entry = gson.fromJson<Entry>(String(plainText), Entry::class.java)
                            val values = ContentValues()
                            values.put(EntryStore.nameColumn, entry.name)
                            values.put(EntryStore.usernameColumn, entry.username)
                            values.put(EntryStore.valueColumn, String(cipherByte))
                            values.put(EntryStore.txHashColumn, "")
                            values.put(EntryStore.stateColumn, EntryStore.statusSyncSuccess)
                            values.put(EntryStore.hashIdColumn, it)
                            database.writableDatabase.use {
                                it.insert(EntryStore.tableName, null, values)
                            }
                        }
                    }
                }catch (e: Exception){
                    Logger.e(e.toString())
                }
                Observable.just(true)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Boolean>(){

                override fun onComplete() {
                }

                override fun onNext(t: Boolean) {
                    isRefreshing = false
                    refreshLayout?.isRefreshing = false
                    broadCast(BroadCastConstants.INVOKE_REFRESH_ENTRYS)
                }

                override fun onError(e: Throwable) {
                    isRefreshing = false
                    refreshLayout?.isRefreshing = false
                    showToast(getString(R.string.sync_data_failed))
                }

            })
}