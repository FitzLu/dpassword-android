package com.dpass.android.activities.password.set

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.dpass.android.R
import com.dpass.android.activities.password.confirm.ConfirmPasswordActivity
import com.dpass.android.base.BaseAccountActivity
import com.dpass.android.common.BundleConstants
import com.dpass.android.utils.Logger
import com.dpass.android.utils.SupportActivityUtil
import com.dpass.android.widgets.PinView

class SetPasswordActivity : BaseAccountActivity(){

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_set_password
    override var hasToolbar: Boolean = false

    private var createTime   = 0.toLong()
    private var keyByte      = byteArrayOf()
    private var isImport     = false

    private var pinView : PinView? = null
    private var password: String = ""
    private var tvStep  : TextView? = null

    private var label = false

    override fun setUpViews(savedInstanceState: Bundle?) {
        tvStep = findViewById(R.id.tvStep)
        findViewById<View>(R.id.tvBack)?.setOnClickListener {
            finish()
        }
        pinView = findViewById(R.id.pinView)
        pinView?.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                password = s?.toString()?:""
                if (password.length == 6){
                    SupportActivityUtil.jumpByIntent(this@SetPasswordActivity,
                            Intent(this@SetPasswordActivity, ConfirmPasswordActivity::class.java)
                                    .also {
                                        it.putExtra(BundleConstants.KEY ,  keyByte)
                                        it.putExtra(BundleConstants.TIME,  createTime)
                                        it.putExtra(BundleConstants.PASS,  password)
                                        it.putExtra(BundleConstants.LABEL, label)
                                        it.putExtra(BundleConstants.IMPORT, isImport)
                                    })
                }
            }

        })

    }

    override fun work(savedInstanceState: Bundle?) {
        keyByte      = intent.getByteArrayExtra(BundleConstants.KEY)?: byteArrayOf()
        createTime   = intent.getLongExtra(BundleConstants.TIME, 0.toLong())
        label        = intent.getBooleanExtra(BundleConstants.LABEL, false)
        isImport     = intent.getBooleanExtra(BundleConstants.IMPORT, false)

        Logger.i("isImport", "$isImport")

        if (label){
            tvStep?.visibility = View.GONE
        }
    }
}