package com.dpass.android.activities.account.import

import android.content.Intent
import android.os.Bundle
import com.dpass.android.R
import com.dpass.android.activities.password.set.SetPasswordActivity
import com.dpass.android.base.BaseAccountActivity
import com.dpass.android.common.BundleConstants
import com.dpass.android.common.SPACE
import com.dpass.android.utils.SupportActivityUtil
import kotlinx.android.synthetic.main.activity_import_account.*
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.wallet.DeterministicSeed

class ImportAccountActivity : BaseAccountActivity() {

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_import_account
    override var hasToolbar: Boolean = false

    override fun setUpViews(savedInstanceState: Bundle?) {
        tvBack?.setOnClickListener {
            finish()
        }
        confirm?.setOnClickListener {
            val mnemonicCodes = arrayListOf<String>()
            mnemonicCodes.add(edtMnemonic1?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic2?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic3?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic4?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic5?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic6?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic7?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic8?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic9?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic10?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic11?.text?.toString()?.trim()?:SPACE)
            mnemonicCodes.add(edtMnemonic12?.text?.toString()?.trim()?:SPACE)

            if (!mnemonicCodes.contains(SPACE)) {
                try {
                    val seed = DeterministicSeed(mnemonicCodes, null, "", System.currentTimeMillis())
                    val keyByte = MnemonicCode.INSTANCE.toEntropy(seed.mnemonicCode)
                    SupportActivityUtil.jumpByIntent(this@ImportAccountActivity,
                            Intent(this@ImportAccountActivity, SetPasswordActivity::class.java)
                                    .also {
                                        it.putExtra(BundleConstants.TIME, seed.creationTimeSeconds)
                                        it.putExtra(BundleConstants.KEY, keyByte)
                                        it.putExtra(BundleConstants.LABEL, true)
                                        it.putExtra(BundleConstants.IMPORT, true)
                                    })
                }catch (e: Exception){
                    showToast(getString(R.string.input_key_words_incorrect))
                }
            }
        }
    }

    override fun work(savedInstanceState: Bundle?) {

    }
}