package com.dpass.android.activities.create

import android.os.Bundle
import android.support.v7.widget.AppCompatCheckBox
import android.view.View
import com.dpass.android.R
import com.dpass.android.activities.account.create.CreateAccountActivity
import com.dpass.android.activities.account.import.ImportAccountActivity
import com.dpass.android.base.BaseAccountActivity
import com.dpass.android.utils.SupportActivityUtil

class CreateActivity : BaseAccountActivity(){

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_create
    override var hasToolbar: Boolean = false

    override fun storeForegroundState() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.primaryBackgroundColor)
    }

    override fun setUpViews(savedInstanceState: Bundle?) {
        findViewById<View>(R.id.btnCreate)?.setOnClickListener {
            SupportActivityUtil.jumpActivity(this@CreateActivity, CreateAccountActivity::class.java)
        }
        findViewById<View>(R.id.btnImport)?.setOnClickListener {
            SupportActivityUtil.jumpActivity(this@CreateActivity, ImportAccountActivity::class.java)
        }
        findViewById<AppCompatCheckBox>(R.id.checkBox)?.isChecked = true
    }

    override fun work(savedInstanceState: Bundle?) {

    }

}
