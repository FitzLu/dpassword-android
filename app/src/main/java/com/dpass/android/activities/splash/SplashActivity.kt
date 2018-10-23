package com.dpass.android.activities.splash

import android.os.Bundle
import android.view.View
import com.dpass.android.R
import com.dpass.android.activities.main.MainActivity
import com.dpass.android.activities.nav.NavActivity
import com.dpass.android.base.BaseAppActivity
import com.dpass.android.utils.SupportActivityUtil
import io.nebulas.nebulas.Nebulas

class SplashActivity: BaseAppActivity() {

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_splash
    override var hasToolbar: Boolean = false

    override fun storeForegroundState() = false

    override fun setUpViews(savedInstanceState: Bundle?) {

    }

    override fun work(savedInstanceState: Bundle?) {
        val address = Nebulas.getMyWalletAddress()
        if (!address.isNullOrEmpty()) {
            findViewById<View>(R.id.splashBackground)?.postDelayed({
                SupportActivityUtil.jumpActivity(this@SplashActivity, MainActivity::class.java)
                finish()
            }, 600)
        }else{
            findViewById<View>(R.id.splashBackground)?.postDelayed({
                SupportActivityUtil.jumpActivity(this@SplashActivity, NavActivity::class.java)
                finish()
            }, 2200)
        }
    }


}