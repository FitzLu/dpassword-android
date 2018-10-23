package com.dpass.android.activities.account.confirm

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.dpass.android.R
import com.dpass.android.activities.account.adapter.ConfirmMnemonicAdapter
import com.dpass.android.activities.account.adapter.ConfirmMnemonicPair
import com.dpass.android.activities.account.adapter.MnemonicAdapter
import com.dpass.android.activities.account.adapter.MutablePair
import com.dpass.android.activities.password.set.SetPasswordActivity
import com.dpass.android.base.BaseAccountActivity
import com.dpass.android.common.BundleConstants
import com.dpass.android.common.MI
import com.dpass.android.dialogs.SingleTipDialog
import com.dpass.android.utils.SupportActivityUtil
import com.fondesa.recyclerviewdivider.RecyclerViewDivider
import kotlinx.android.synthetic.main.activity_confirm_account.*
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.wallet.DeterministicSeed

class ConfirmAccountActivity : BaseAccountActivity() {

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_confirm_account
    override var hasToolbar: Boolean = false

    //正确顺序的词组
    private val originalCodes = arrayListOf<String>()
    //打乱顺序的词组
    private val shuffleCodes  = arrayListOf<String>()

    private var confirmAdapter: ConfirmMnemonicAdapter? = null
    private var detachAdapter : MnemonicAdapter? = null

    private var createTime   = 0.toLong()
    private var keyByte      = byteArrayOf()

    private var singleDialog: SingleTipDialog? = null

    override fun setUpViews(savedInstanceState: Bundle?) {
        tvBack?.setOnClickListener {
            finish()
        }
        recyclerViewConfirm?.layoutManager = GridLayoutManager(this, 3).also {
            it.orientation = GridLayoutManager.VERTICAL
        }
        recyclerViewOriginal?.layoutManager = GridLayoutManager(this, 3).also {
            it.orientation = GridLayoutManager.VERTICAL
        }
        confirmAdapter = ConfirmMnemonicAdapter()
        detachAdapter  = MnemonicAdapter()
        recyclerViewConfirm?.adapter  = confirmAdapter
        recyclerViewOriginal?.adapter = detachAdapter
        tvForget?.setOnClickListener {
            finish()
        }
    }

    override fun work(savedInstanceState: Bundle?) {
        originalCodes.clear()
        shuffleCodes.clear()

        confirmAdapter?.mnemonicCode?.clear()
        detachAdapter?.mnemonicCode?.clear()

        keyByte    = intent.getByteArrayExtra(BundleConstants.KEY)
        createTime = intent.getLongExtra(BundleConstants.TIME, 0.toLong())

        val codes = intent.getStringArrayListExtra(BundleConstants.LIST)
        if (codes.isNotEmpty()){
            codes.mapTo(originalCodes, {it})
            codes.shuffle()
            codes.mapTo(shuffleCodes, {it})
        }

        RecyclerViewDivider.with(this)
                .color(resources.getColor(R.color.divider))
                .hideLastDivider()
                .build()
                .addTo(recyclerViewConfirm)

        confirmAdapter?.mnemonicCode?.clear()
        originalCodes.forEach {
            confirmAdapter?.mnemonicCode?.add(ConfirmMnemonicPair(MI, 0))
        }
        confirmAdapter?.notifyDataSetChanged()

        val adapterData = arrayListOf<MutablePair<String, Boolean>>()
        shuffleCodes.forEach {
            adapterData.add(MutablePair(it, true))
        }
        detachAdapter?.mnemonicCode = adapterData
        detachAdapter?.mOnItemClickListener = object : MnemonicAdapter.OnItemClickListener {
            override fun onClicked(mnemonic: String, adapterPosition: Int) {
                confirmAdapter?.mnemonicCode?.first { it.status == 0 }?.let {
                    it.code = mnemonic
                    it.status = 1
                }
                confirmAdapter?.notifyDataSetChanged()

                detachAdapter?.mnemonicCode?.first { it.first == mnemonic }
                        ?.let { it.second = false }
                detachAdapter?.notifyDataSetChanged()

                if (detachAdapter != null && detachAdapter!!.allSelected()) {
                    if (compareInputEqual()) {
                        if (verifyMnemonicToEntropy()) {
                            recyclerViewConfirm?.postDelayed({
                                SupportActivityUtil.jumpByIntent(this@ConfirmAccountActivity,
                                        Intent(this@ConfirmAccountActivity, SetPasswordActivity::class.java)
                                                .also {
                                                    it.putExtra(BundleConstants.KEY , keyByte)
                                                    it.putExtra(BundleConstants.TIME, createTime)
                                                })
                            }, 200)
                        }else{
                            showToast(getString(R.string.key_verify_failed))
                        }
                    }else{
                        if (singleDialog == null){
                            singleDialog = SingleTipDialog.newInstance(this@ConfirmAccountActivity, getString(R.string.mnemonic_confirm_failed))
                            singleDialog?.setCanceledOnTouchOutside(false)
                            singleDialog?.mOnConfirmClickListener = object: SingleTipDialog.OnConfirmClickListener {

                                override fun onClicked() {
                                    confirmAdapter?.mnemonicCode?.clear()
                                    originalCodes.forEach {
                                        confirmAdapter?.mnemonicCode?.add(ConfirmMnemonicPair(MI, 0))
                                    }
                                    confirmAdapter?.notifyDataSetChanged()

                                    val ad = arrayListOf<MutablePair<String, Boolean>>()
                                    shuffleCodes.shuffle()
                                    shuffleCodes.forEach {
                                        ad.add(MutablePair(it, true))
                                    }
                                    detachAdapter?.mnemonicCode = ad
                                    detachAdapter?.notifyDataSetChanged()
                                }

                            }
                        }
                        if (singleDialog != null && !singleDialog!!.isShowing){
                            singleDialog?.show()
                        }
                    }
                }
            }
        }
        detachAdapter?.notifyDataSetChanged()
    }

    private fun compareInputEqual(): Boolean{
        if (confirmAdapter?.mnemonicCode == null){
            return false
        }
        if (confirmAdapter!!.mnemonicCode.size != originalCodes.size){
            return false
        }
        confirmAdapter!!.mnemonicCode.withIndex().forEach {
            if (it.value.code != originalCodes[it.index]){
                return false
            }
        }

        return true
    }

    private fun verifyMnemonicToEntropy(): Boolean{
        if (keyByte.isEmpty()) return false

        val confirmCodes = arrayListOf<String>()
        confirmAdapter!!.mnemonicCode.forEach {
            confirmCodes.add(it.code)
        }
        val seed = DeterministicSeed(confirmCodes, null, "", System.currentTimeMillis())
        val entropy = MnemonicCode.INSTANCE.toEntropy(seed.mnemonicCode) ?: return false

        return entropy.contentEquals(keyByte)
    }

}