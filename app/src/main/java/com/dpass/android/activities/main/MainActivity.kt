package com.dpass.android.activities.main

import android.arch.lifecycle.Observer
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.dpass.android.R
import com.dpass.android.activities.lock.ScreenLockActivity
import com.dpass.android.activities.main.account.AccountFragment
import com.dpass.android.activities.main.entrys.EntrysFragment
import com.dpass.android.base.BaseAppActivity
import com.dpass.android.bean.*
import com.dpass.android.common.BroadCastConstants
import com.dpass.android.common.BundleConstants
import com.dpass.android.common.CONTRACT_ADDRESS
import com.dpass.android.common.GAS_LIMIT
import com.dpass.android.live.AccountStateLiveData
import com.dpass.android.net.AccountRequest
import com.dpass.android.net.NetWorker
import com.dpass.android.net.Response
import com.dpass.android.net.TransactionRequest
import com.dpass.android.stroage.KeyShareLiveData
import com.dpass.android.stroage.database
import com.dpass.android.stroage.loadLocalEntrys
import com.dpass.android.utils.Logger
import com.dpass.android.utils.SupportActivityUtil
import com.google.gson.GsonBuilder
import io.nebulas.core.Address
import io.nebulas.core.Transaction
import io.nebulas.core.TransactionCallPayload
import io.nebulas.crypto.Crypto
import io.nebulas.crypto.keystore.Algorithm
import io.nebulas.crypto.keystore.secp256k1.ECPrivateKey
import io.nebulas.nebulas.Nebulas
import io.nebulas.util.ByteUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.math.BigInteger
import java.util.concurrent.TimeUnit

class MainActivity : BaseAppActivity() {

    override var mTag: String        = javaClass.simpleName
    override var layoutResId: Int    = R.layout.activity_main
    override var hasToolbar: Boolean = false

    private val mEntrysFragment : EntrysFragment  by lazy { EntrysFragment.newInstance(null) }
    private val mAccountFragment: AccountFragment by lazy { AccountFragment.newInstance(null) }

    private var mBottomNav: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        KeyShareLiveData.get().observe(this, Observer<ByteArray> { t->
            if (t != null && t.isNotEmpty()){
                loadLocalEntrys(this@MainActivity)
            }
        })
        EntrysLiveData.get().observe(this, Observer<ArrayList<EntryStore>> { t->
            t?.forEach {
              if (it.state == EntryStore.statusSyncing || it.state == EntryStore.statusSyncingModify
                    && it.txHash.isNotEmpty()){
                  askTxHashStatus(it, 2.toLong())
              }
            }
        })
        super.onCreate(savedInstanceState)
    }

    override fun setUpViews(savedInstanceState: Bundle?) {
        mBottomNav = findViewById(R.id.mBottomNav)
        mBottomNav?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.action_password -> {
                    SupportActivityUtil.switchFragmentInActivity(supportFragmentManager,
                            mAccountFragment, mEntrysFragment, R.id.container)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_account -> {
                    SupportActivityUtil.switchFragmentInActivity(supportFragmentManager,
                            mEntrysFragment, mAccountFragment, R.id.container)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    override fun work(savedInstanceState: Bundle?) {
        Logger.i("nasAddress", Nebulas.getMyWalletAddress())
        registersBroadCast(BroadCastConstants.COMPLETE_EDIT_ENTRY,
                BroadCastConstants.INVOKE_REFRESH_ENTRYS,
                BroadCastConstants.ASK_TX_STATUS,
                BroadCastConstants.DELETE_ENTRY)
        SupportActivityUtil.switchFragmentInActivity(supportFragmentManager,
                mAccountFragment, mEntrysFragment, R.id.container)
    }

    override fun onResume() {
        super.onResume()
        obtainAccountState()
    }

    override fun onReceiveBroadCast(context: Context?, intent: Intent?) {
        super.onReceiveBroadCast(context, intent)
        when(intent?.action){
            BroadCastConstants.COMPLETE_EDIT_ENTRY, BroadCastConstants.INVOKE_REFRESH_ENTRYS ->
                loadLocalEntrys(this@MainActivity)
            BroadCastConstants.INVOKE_REFRESH_ACCOUNT ->
                obtainAccountState()
            BroadCastConstants.ASK_TX_STATUS -> {
                try {
                    val entryStore = intent.getSerializableExtra(BundleConstants.ENTRYSTORE) as EntryStore
                    if (entryStore.txHash.isNotEmpty()) {
                        askTxHashStatus(entryStore, 30.toLong())
                    }
                }catch (e: Exception){
                    Logger.e(e.toString())
                }
            }
            BroadCastConstants.DELETE_ENTRY -> {
                try {
                    val entryStore = intent.getSerializableExtra(BundleConstants.DATA) as EntryStore
                    val nonce = AccountStateLiveData.get().value?.nonce
                    AccountStateLiveData.get().addNonce()
                    val myAddress = Nebulas.getMyWalletAddress()
                    val contractAddress = CONTRACT_ADDRESS
                    val privateKey = KeyShareLiveData.get().value
                    if (entryStore.hashId.isNotEmpty() && nonce != null && myAddress.isNotEmpty() && privateKey != null && privateKey.isNotEmpty()) {
                        delete(entryStore, nonce.toLong(), myAddress, contractAddress, privateKey)
                    }
                }catch (e: Exception){
                    Logger.e(e.toString())
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (!Nebulas.getMyWalletAddress().isNullOrEmpty()) {
            try {
                val locker = Intent(this, ScreenLockActivity::class.java)
                SupportActivityUtil.jumpByIntent(this, locker)
            } catch (e: Exception) {
                Logger.e("dpassword", e.toString())
            }
        }
    }

    private var isLoading = false
    private fun obtainAccountState(){
        if (isLoading){
            return
        }
        val address = Nebulas.getMyWalletAddress()
        if (address.isNullOrEmpty()){
            return
        }
        var params = ""
        try {
            params = GsonBuilder().create().toJson(AccountRequest(address))
        }catch (e: Exception){
            Logger.i(e.toString())
        }
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params)
        isLoading = true
        NetWorker.callRxApiLauncher().accountState(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<AccountState>>(){

                    override fun onComplete() {

                    }

                    override fun onNext(t: Response<AccountState>) {
                        isLoading = false
                        AccountStateLiveData.get().postValue(t.result)
                    }

                    override fun onError(e: Throwable) {
                        isLoading = false
                    }


                }).also { enqueueToComposite(it) }
    }

    private val queueArray = ArrayList<String>()
    private fun askTxHashStatus(entryStore: EntryStore, delay: Long){
        if (queueArray.contains(entryStore.txHash)){
            Logger.w("askTxHashStatus", "${entryStore.txHash} already in the queue")
            return
        }
        queueArray.add(entryStore.txHash)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"hash\":\"${entryStore.txHash}\"}")
        Observable.timer(delay, TimeUnit.SECONDS)
                .concatMap {
                    NetWorker.callRxApiLauncher().transactionReceipt(requestBody)
                }
                .doOnNext {
                    //status Transaction status, 0 failed, 1 success, 2 pending.
                    Logger.i("askTxHashStatus", "status ${it.result.status}")
                    when {
                        it.result.status == 1 -> {
                            val values = ContentValues()
                            values.put(EntryStore.stateColumn, EntryStore.statusSyncSuccess)
                            database.writableDatabase.update(EntryStore.tableName,
                                    values, "id = ${entryStore.id}", null)
                            Logger.i("askTxHashStatus", "success")
                            loadLocalEntrys(this@MainActivity)
                        }
                        it.result.status == 0 -> {
                            val values = ContentValues()
                            if (entryStore.state == EntryStore.statusSyncing){
                                //sync
                                values.put(EntryStore.stateColumn, EntryStore.statusSyncFailed)
                                database.writableDatabase.update(EntryStore.tableName, values, "id = ${entryStore.id}", null)
                            }else if (entryStore.state == EntryStore.statusSyncingModify){
                                //sync modify
                                values.put(EntryStore.stateColumn, EntryStore.statusSyncModifyFailed)
                                database.writableDatabase.update(EntryStore.tableName, values, "id = ${entryStore.id}", null)
                            }
                            Logger.i("askTxHashStatus", "failed")
                            loadLocalEntrys(this@MainActivity)
                        }
                        else -> Logger.i("askTxHashStatus", "pending")
                    }
                }
                .concatMap {
                    Observable.just(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<TransactionReceipt>>(){

                    override fun onComplete() {}

                    override fun onNext(t: Response<TransactionReceipt>) {
                        queueArray.remove(entryStore.txHash)
                    }

                    override fun onError(e: Throwable) {
                        queueArray.remove(entryStore.txHash)
                    }

                }).also { enqueueToComposite(it) }
    }

    private fun delete(entryStore: EntryStore, lastNonce: Long, myAddress: String, targetAddress: String, privateKeyRaw: ByteArray){
        Observable.zip(
                prepareArgs(entryStore),
                NetWorker.callRxApiLauncher().gasPrice(),
                BiFunction<String, Response<GasPrice>, Pair<String, GasPrice>> { t1, t2 ->
                    Pair(t1, t2.result)
                })
                .concatMap { t ->
                    val chainID = 1 //1 mainet,1001 testnet, 100 default private
                    val from = Address.ParseFromString(myAddress)
                    val to = Address.ParseFromString(targetAddress)
                    val value = BigInteger("0")
                    val nonce = lastNonce + 1
                    val payloadType: Transaction.PayloadType = Transaction.PayloadType.CALL
                    val payload = TransactionCallPayload("delete", t.first).toBytes()
                    val gasPrice = BigInteger(t.second.gasPrice) // 0 < gasPrice < 10^12
                    val gasLimit = BigInteger(GAS_LIMIT) // 20000 < gasPrice < 50*10^9
                    val tx = Transaction(chainID, from, to, value, nonce, payloadType, payload, gasPrice, gasLimit)
                    val privateKey = ECPrivateKey(privateKeyRaw)
                    val signature = Crypto.NewSignature(Algorithm.SECP256K1)
                    signature.initSign(privateKey)
                    tx.sign(signature)
                    tx.hash

                    val txString = ByteUtils.Base64ToString(tx.toProto())
                    val params = GsonBuilder().create().toJson(TransactionRequest(txString))
                    val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params)
                    NetWorker.callRxApiLauncher().rawtransaction(requestBody)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<TxHash>>() {

                    override fun onComplete() {
                    }

                    override fun onNext(t: Response<TxHash>) {
                        if (t.result.txHash.isNotEmpty()) {
                            val value = ContentValues()
                            value.put(EntryStore.stateColumn, EntryStore.statusSyncingDelete)
                            value.put(EntryStore.txHashColumn, t.result.txHash)
                            database.writableDatabase.use {
                                it.update(EntryStore.tableName, value, "id = ${entryStore.id}", null)
                            }
                            Logger.i("delete", "delete success")
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logger.i("delete", "delete failed")
                        val value = ContentValues()
                        value.put(EntryStore.stateColumn, EntryStore.statusSyncDeleteFailed)
                        database.writableDatabase.use {
                            it.update(EntryStore.tableName, value, "id = ${entryStore.id}", null)
                        }
                    }

                }).also { enqueueToComposite(it) }
    }

    private fun prepareArgs(entryStore: EntryStore): Observable<String> {
        return Observable.defer{
            val args         = "[\"${entryStore.hashId}\"]"
            Logger.i("args", args)
            Logger.i("hash", entryStore.hashId)
            Observable.just(args)
        }
    }

}
