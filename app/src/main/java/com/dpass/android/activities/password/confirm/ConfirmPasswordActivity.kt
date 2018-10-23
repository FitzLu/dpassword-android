package com.dpass.android.activities.password.confirm

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.dpass.android.R
import com.dpass.android.activities.main.MainActivity
import com.dpass.android.base.BaseAccountActivity
import com.dpass.android.bean.*
import com.dpass.android.common.BroadCastConstants
import com.dpass.android.common.BundleConstants
import com.dpass.android.common.CONTRACT_ADDRESS
import com.dpass.android.common.GAS_LIMIT
import com.dpass.android.dialogs.ProgressDialog
import com.dpass.android.dialogs.SingleTipDialog
import com.dpass.android.live.AccountStateLiveData
import com.dpass.android.net.AccountRequest
import com.dpass.android.net.NetWorker
import com.dpass.android.net.Response
import com.dpass.android.stroage.KeyShareLiveData
import com.dpass.android.stroage.SharedPreferencesManager
import com.dpass.android.stroage.database
import com.dpass.android.utils.Logger
import com.dpass.android.utils.SupportActivityUtil
import com.dpass.android.widgets.PinView
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
import org.json.JSONObject

class ConfirmPasswordActivity : BaseAccountActivity(){

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_confirm_password
    override var hasToolbar: Boolean = false

    private var keyByte      = byteArrayOf()
    private var createTime   = 0.toLong()
    private var lastPass     = ""
    private var isImport     = false

    private var pinView : PinView? = null
    private var password: String = ""
    private var tvStep  : TextView? = null

    private var singleDialog: SingleTipDialog? = null

    override fun setUpViews(savedInstanceState: Bundle?) {
        tvStep = findViewById(R.id.tvStep)
        findViewById<View>(R.id.tvBack)?.setOnClickListener {
            finish()
        }
        pinView = findViewById(R.id.pinView)
        pinView?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                password = s?.toString()?:""
                if (password.length == 6){
                    if (lastPass == password ){
                        if (keyByte.isNotEmpty()) {
                            val address = Nebulas.createNewAccount(password, keyByte)
                            if (address != null && address.string().isNotEmpty()) {
                                KeyShareLiveData.get().postValue(Nebulas.getMyPrivateKey(password))
                                if (isImport){
                                    obtainAccountState(Nebulas.getMyPrivateKey(password))
                                }else {
                                    SupportActivityUtil.jumpActivity(this@ConfirmPasswordActivity,
                                            MainActivity::class.java)
                                    broadCast(BroadCastConstants.COMPLETE_CREATE_ACCOUNT)
                                }
                            } else {
                                SingleTipDialog.newInstance(this@ConfirmPasswordActivity, getString(R.string.create_account_failed))
                                        .apply { setCanceledOnTouchOutside(false) }
                                        .show()
                            }
                        }else{
                            showToast(getString(R.string.key_verify_failed))
                        }
                    }else{
                        if (singleDialog == null){
                            singleDialog = SingleTipDialog.newInstance(this@ConfirmPasswordActivity, getString(R.string.password_not_equal))
                            singleDialog?.setCanceledOnTouchOutside(false)
                            singleDialog?.mOnConfirmClickListener = object : SingleTipDialog.OnConfirmClickListener {
                                override fun onClicked() {
                                    pinView?.setText("")
                                }
                            }
                        }
                        if (singleDialog != null && !singleDialog!!.isShowing){
                            singleDialog?.show()
                        }
                    }
                }
            }

        })
    }

    override fun work(savedInstanceState: Bundle?) {
        keyByte      = intent.getByteArrayExtra(BundleConstants.KEY)?: byteArrayOf()
        createTime   = intent.getLongExtra(BundleConstants.TIME, 0.toLong())
        lastPass     = intent.getStringExtra(BundleConstants.PASS)?:""
        isImport     = intent.getBooleanExtra(BundleConstants.IMPORT, false)

        Logger.i("isImport", "$isImport")

        val label    = intent.getBooleanExtra(BundleConstants.LABEL, false)
        if (label){
            tvStep?.visibility = View.GONE
        }
    }

    private val mProgressDialog: ProgressDialog by lazy {
        ProgressDialog(this@ConfirmPasswordActivity).also {
            it.setCanceledOnTouchOutside(false)
            it.setCancelable(false)
        }
    }
    private var isLoading = false
    private fun obtainAccountState(privateKeyRaw: ByteArray){
        if (isLoading){
            return
        }
        isLoading = true
        if (!mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
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
                        resultJson.keys().forEach {
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
                                database.writableDatabase.insert(EntryStore.tableName, null, values)
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
                        mProgressDialog.dismiss()
                        SharedPreferencesManager.get().showTipPopWindow = false
                        SharedPreferencesManager.get().showTipDialog    = false
                        KeyShareLiveData.get().postValue(Nebulas.getMyPrivateKey(password))
                        SupportActivityUtil.jumpActivity(this@ConfirmPasswordActivity,
                                MainActivity::class.java)
                        isLoading = false
                        broadCast(BroadCastConstants.COMPLETE_CREATE_ACCOUNT)
                    }

                    override fun onError(e: Throwable) {
                        mProgressDialog.dismiss()
                        isLoading = false
                        Logger.e(e.toString())
                        showToast(getString(R.string.sync_data_failed))
                    }

                })
    }

    override fun onDestroy() {
        try{
            mProgressDialog.dismiss()
        }catch (e: Exception){

        }
        super.onDestroy()
    }
}