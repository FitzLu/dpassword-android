package com.dpass.android.activities.account.create

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import com.dpass.android.R
import com.dpass.android.activities.account.adapter.MnemonicAdapter
import com.dpass.android.activities.account.adapter.MutablePair
import com.dpass.android.activities.account.confirm.ConfirmAccountActivity
import com.dpass.android.base.BaseAccountActivity
import com.dpass.android.common.BundleConstants
import com.dpass.android.dialogs.SingleTipDialog
import com.dpass.android.utils.Logger
import com.dpass.android.utils.SupportActivityUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.bitcoinj.wallet.DeterministicSeed
import java.security.SecureRandom


class CreateAccountActivity : BaseAccountActivity() {

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_create_account
    override var hasToolbar: Boolean = false

    private var mnemonicCode = arrayListOf<String>()
    private var keyByte      = byteArrayOf()
    private var createTime   = 0.toLong()

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter     : MnemonicAdapter? = null
    private var btnConfirm   : Button?       = null

    private var seedCreated  = false

    private var singleDialog: SingleTipDialog? = null

    override fun setUpViews(savedInstanceState: Bundle?) {
        findViewById<View>(R.id.tvBack)?.setOnClickListener {
            finish()
        }
        mRecyclerView = findViewById(R.id.recyclerView)
        btnConfirm    = findViewById(R.id.btnConfirm)

        mRecyclerView?.layoutManager = GridLayoutManager(this, 3).also {
            it.orientation = GridLayoutManager.VERTICAL
        }
        mAdapter = MnemonicAdapter()
        mRecyclerView?.adapter = mAdapter
        btnConfirm?.setOnClickListener {
            if (seedCreated) {
                SupportActivityUtil.jumpByIntent(this@CreateAccountActivity,
                        Intent(this@CreateAccountActivity, ConfirmAccountActivity::class.java).also {
                            it.putExtra(BundleConstants.LIST, mnemonicCode)
                            it.putExtra(BundleConstants.TIME, createTime)
                            it.putExtra(BundleConstants.KEY , keyByte)
                        })
            }
        }
    }

    override fun work(savedInstanceState: Bundle?) {
        createBip39Seed()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Pair<DeterministicSeed, ByteArray>>(){
                    override fun onComplete() {}

                    override fun onNext(t: Pair<DeterministicSeed, ByteArray>) {
                        if (t.second.isNotEmpty() && t.first.mnemonicCode != null) {
                            seedCreated = true
                            keyByte  = t.second
                            t.first.mnemonicCode!!.mapTo(mnemonicCode, {it})
                            createTime = t.first.creationTimeSeconds
                            val adapterData = arrayListOf<MutablePair<String, Boolean>>()
                            mnemonicCode.forEach {
                                adapterData.add(MutablePair(it, true))
                            }
                            mAdapter?.mnemonicCode = adapterData
                            mAdapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Logger.e(e.toString())
                    }

                }).also { enqueueToComposite(it) }

        if (singleDialog == null){
            singleDialog = SingleTipDialog.newInstance(this, resources.getString(R.string.save_mnemonic))
            singleDialog?.setCanceledOnTouchOutside(false)
        }
        if (singleDialog != null && !singleDialog!!.isShowing){
            singleDialog?.show()
        }
    }

    private fun createBip39Seed(): Observable<Pair<DeterministicSeed, ByteArray>> {
        return Observable.defer {
            // Do some long running operation
            //生成32字节随机序列
            val entropy = ByteArray(128 / 8)
            SecureRandom().nextBytes(entropy)
            //Bit39 生成种子和助记词
            val seed = DeterministicSeed(entropy, "", System.currentTimeMillis())
            Observable.just(Pair(seed, entropy))
        }
    }

}