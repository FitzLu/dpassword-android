package com.dpass.android.activities.main.entrys

import android.arch.lifecycle.Observer
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.dpass.android.R
import com.dpass.android.activities.entry.EditEntryActivity
import com.dpass.android.activities.entry.EntryDetailActivity
import com.dpass.android.activities.main.EntrysLiveData
import com.dpass.android.base.BaseVCFragment
import com.dpass.android.bean.EntryStore
import com.dpass.android.bean.GasPrice
import com.dpass.android.bean.TxHash
import com.dpass.android.common.BroadCastConstants
import com.dpass.android.common.BundleConstants
import com.dpass.android.common.CONTRACT_ADDRESS
import com.dpass.android.common.GAS_LIMIT
import com.dpass.android.dialogs.TipAlertDialog
import com.dpass.android.live.AccountStateLiveData
import com.dpass.android.net.NetWorker
import com.dpass.android.net.Response
import com.dpass.android.net.TransactionRequest
import com.dpass.android.stroage.KeyShareLiveData
import com.dpass.android.stroage.SharedPreferencesManager
import com.dpass.android.stroage.database
import com.dpass.android.utils.Logger
import com.dpass.android.utils.Perception
import com.dpass.android.utils.SupportActivityUtil
import com.dpass.android.widgets.TipPopupWindow
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

class EntrysFragment : BaseVCFragment() {

    companion object {
        fun newInstance(p0: Bundle?) = EntrysFragment().also {
            if (p0 != null) it.arguments = p0
        }
    }

    override var layoutResId: Int = R.layout.fragment_entrys

    private var llEmptyView : LinearLayout?  = null
    private var recyclerView: RecyclerView?  = null
    private var mAdapter    : EntrysAdapter? = null
    var refreshLayout: SwipeRefreshLayout? = null

    private var mTipPopupWindow: TipPopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EntrysLiveData.get().observe(this, Observer<ArrayList<EntryStore>> { t->
            if (t != null && t.isNotEmpty()){
                llEmptyView?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                mAdapter?.entrys = t
                mAdapter?.entrys?.reverse()
                mAdapter?.notifyDataSetChanged()
                Logger.i(javaClass.simpleName, "refresh")
            }else{
                llEmptyView?.visibility = View.VISIBLE
                recyclerView?.visibility = View.GONE
                mAdapter?.entrys?.clear()
                mAdapter?.notifyDataSetChanged()
            }
        })
    }

    override fun setUpViews(root: View, savedInstanceState: Bundle?) {
        with(root){
            refreshLayout = findViewById(R.id.refreshLayout)
            llEmptyView = findViewById(R.id.llEmptyView)
            recyclerView = findViewById(R.id.recyclerView)
            refreshLayout?.setOnRefreshListener {
                if (mContext != null) {
                    Perception.getSharedKeyOrLock(mContext!!).apply {
                        if (isAlive){
                            refreshData(key, mContext!!.database, mContext!!)
                        }
                    }
                }
            }
            if (mContext != null) {
                recyclerView?.layoutManager = LinearLayoutManager(mContext!!)
                        .also { it.orientation = LinearLayoutManager.VERTICAL }
                mAdapter = EntrysAdapter()
                mAdapter?.entrysActionListener = object : EntrysAdapter.EntrysActionListener{

                    override fun onBindFirstEntry(x: Float, y: Float, syncImageView: ImageView) {
                        if (mContext != null && SharedPreferencesManager.get().showTipPopWindow){
                            syncImageView.postDelayed({ mTipPopupWindow?.showToStartOf(syncImageView) }, 300)
                            SharedPreferencesManager.get().showTipPopWindow = false
                        }
                    }

                    override fun onItemClicked(entryStore: EntryStore) {
                        if (mContext != null) {
                            SupportActivityUtil.jumpByIntent(mContext!!, Intent(mContext!!, EntryDetailActivity::class.java)
                                    .also { it.putExtra(BundleConstants.DATA, entryStore) })
                        }
                    }

                    override fun onCopyClicked(entryStore: EntryStore) {

                    }

                    override fun onSyncClicked(entryStore: EntryStore, adapterPosition: Int) {
                        handleSyncClicked(entryStore, adapterPosition)
                    }

                }
                recyclerView?.adapter = mAdapter
            }
            findViewById<View>(R.id.ivAdd)?.setOnClickListener {
                if (mContext != null) {
                    SupportActivityUtil.jumpByIntent(mContext!!,
                            Intent(mContext!!, EditEntryActivity::class.java))
                }
            }
            findViewById<View>(R.id.tvAddEntry)?.setOnClickListener {
                if (mContext != null) {
                    SupportActivityUtil.jumpByIntent(mContext!!,
                            Intent(mContext!!, EditEntryActivity::class.java))
                }
            }
        }
    }

    override fun workOnViewFirstCreated(savedInstanceState: Bundle?) {
        if (mContext != null) {
            mTipPopupWindow = TipPopupWindow(mContext!!)
        }
    }

    override fun work(savedInstanceState: Bundle?) {

    }

    private fun handleSyncClicked(entryStore: EntryStore, adapterPosition: Int){
        if (entryStore.state == EntryStore.statusLocal || entryStore.state == EntryStore.statusSyncFailed ||
                entryStore.state == EntryStore.statusModify || entryStore.state == EntryStore.statusSyncModifyFailed){
            if (SharedPreferencesManager.get().showTipDialog){
                TipAlertDialog(mContext!!, getString(R.string.first_sync_tip),
                        mContext!!.resources.getColor(R.color.primaryTextColor))
                        .also { it.mOnConfirmClickListener = object : TipAlertDialog.OnConfirmClickListener{
                            override fun onClicked() {
                                it.dismiss()
                                SharedPreferencesManager.get().showTipDialog = false
                                executeSync(entryStore, adapterPosition)
                            }
                        }}.show()
            }else{
                executeSync(entryStore, adapterPosition)
            }
        }
    }

    private fun executeSync(entryStore: EntryStore, adapterPosition: Int){
        val balance = AccountStateLiveData.get().value?.balance?:"0"
        if (Perception.checkBalanceEnough(balance)){
            if (entryStore.state == EntryStore.statusLocal || entryStore.state == EntryStore.statusSyncFailed) {
                val nonce = AccountStateLiveData.get().value?.nonce
                AccountStateLiveData.get().addNonce()
                val myAddress = Nebulas.getMyWalletAddress()
                val contractAddress = CONTRACT_ADDRESS
                val privateKey = KeyShareLiveData.get().value
                if (mContext != null && entryStore.value.isNotEmpty() && nonce != null && myAddress.isNotEmpty()
                        && privateKey != null && privateKey.isNotEmpty()) {
                    entryStore.state = EntryStore.statusSyncing
                    mAdapter?.notifyItemChanged(adapterPosition)
                    upload("save", entryStore, adapterPosition, nonce.toLong(), myAddress, contractAddress, privateKey)
                }
            } else if(entryStore.state == EntryStore.statusModify || entryStore.state == EntryStore.statusSyncModifyFailed){
                val nonce = AccountStateLiveData.get().value?.nonce
                AccountStateLiveData.get().addNonce()
                val myAddress = Nebulas.getMyWalletAddress()
                val contractAddress = CONTRACT_ADDRESS
                val privateKey = KeyShareLiveData.get().value
                if (mContext != null && entryStore.value.isNotEmpty() && nonce != null && myAddress.isNotEmpty()
                        && privateKey != null && privateKey.isNotEmpty()) {
                    entryStore.state = EntryStore.statusSyncing
                    mAdapter?.notifyItemChanged(adapterPosition)
                    upload("update", entryStore, adapterPosition, nonce.toLong(), myAddress, contractAddress, privateKey)
                }
            }
        }else{
            if(mContext != null) {
                TipAlertDialog(mContext!!, getString(R.string.insufficient_balance), null).also {
                    it.mOnConfirmClickListener = object : TipAlertDialog.OnConfirmClickListener{
                        override fun onClicked() {
                            it.dismiss()
                        }
                    }
                }.show()
            }
        }
    }

    private fun upload(func: String, entryStore: EntryStore, adapterPosition: Int, lastNonce: Long, myAddress: String, targetAddress: String, privateKeyRaw: ByteArray){
        Logger.i("upload", func)
        if (func != "save" && func != "update"){
            Logger.w("upload", "unsupported func $func")
            return
        }
        Observable.zip(
                prepareArgs(entryStore),
                NetWorker.callRxApiLauncher().gasPrice(),
                BiFunction<String, Response<GasPrice>, Pair<String, GasPrice>> { t1, t2 ->
                    Pair(t1, t2.result)
                })
                .concatMap { t->
                    val chainID = 1 //1 mainet,1001 testnet, 100 default private
                    val from = Address.ParseFromString(myAddress)
                    val to = Address.ParseFromString(targetAddress)
                    val value = BigInteger("0")
                    val nonce = lastNonce + 1
                    val payloadType: Transaction.PayloadType = Transaction.PayloadType.CALL
                    val payload = TransactionCallPayload(func, t.first).toBytes()
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
                .subscribeWith(object : DisposableObserver<Response<TxHash>>(){

                    override fun onComplete() {
                    }

                    override fun onNext(t: Response<TxHash>) {
                        if (t.result.txHash.isNotEmpty()) {
                            entryStore.txHash = t.result.txHash
                            mAdapter?.notifyItemChanged(adapterPosition)
                            val values = ContentValues()
                            values.put(EntryStore.valueColumn, entryStore.value)
                            values.put(EntryStore.stateColumn, if (func == "save") EntryStore.statusSyncing else EntryStore.statusSyncingModify)
                            values.put(EntryStore.txHashColumn, t.result.txHash)
                            mContext!!.database.writableDatabase
                                    .update(EntryStore.tableName, values, "id = ${entryStore.id}", null)
                            val intent = Intent(BroadCastConstants.ASK_TX_STATUS)
                            intent.putExtra(BundleConstants.ENTRYSTORE, entryStore)
                            broadCast(intent)
                        }
                    }

                    override fun onError(e: Throwable) {
                        val values = ContentValues()
                        values.put(EntryStore.valueColumn, entryStore.value)
                        values.put(EntryStore.stateColumn, if (func == "save") EntryStore.statusSyncFailed else EntryStore.statusSyncModifyFailed)
                        values.put(EntryStore.txHashColumn, "")
                        mContext!!.database.writableDatabase
                                .update(EntryStore.tableName, values, "id = ${entryStore.id}", null)
                        entryStore.state = EntryStore.statusSyncFailed
                        mAdapter?.notifyItemChanged(adapterPosition)
                    }

                }).also { enqueueToComposite(it) }
    }

    private fun prepareArgs(entryStore: EntryStore): Observable<String>{
        return Observable.defer{
            val cipherText   = entryStore.value.toByteArray()
            val cipherString = ByteUtils.Base64ToString(cipherText)
            val args         = "[\"$cipherString\",\"${entryStore.hashId}\"]"
            Logger.i("args", args)
            Logger.i("hash", entryStore.hashId)
            Observable.just(args)
        }
    }

    var isRefreshing = false

}