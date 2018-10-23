package com.dpass.android

import android.content.Intent
import android.support.multidex.MultiDexApplication
import com.dpass.android.activities.lock.ScreenLockActivity
import com.dpass.android.net.NetWorker
import com.dpass.android.stroage.KeyShareLiveData
import com.dpass.android.stroage.SharedPreferencesManager
import com.dpass.android.utils.AppForegroundStateManager
import com.dpass.android.utils.Logger
import com.dpass.android.utils.SupportActivityUtil
import io.nebulas.nebulas.Nebulas

class App : MultiDexApplication(){

    override fun onCreate() {
        super.onCreate()
        NetWorker.initialize(applicationContext)
        SharedPreferencesManager.initialize(applicationContext)
        Nebulas.initialize(applicationContext)
        observeAppState()
    }

    private fun observeAppState(){
        AppForegroundStateManager.getInstance().addListener { newState ->
            if (AppForegroundStateManager.AppForegroundState.IN_FOREGROUND == newState) {
                // App just entered the foreground. Do something here!
                Logger.w("dpassword", "enter foreground")
                if (!Nebulas.getMyWalletAddress().isNullOrEmpty()) {
                    try {
                        val intent = Intent(this, ScreenLockActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        SupportActivityUtil.jumpByIntent(this, intent)
                    } catch (e: Exception) {
                        Logger.e("dpassword", e.toString())
                    }
                }
            } else {
                // App just entered the background. Do something here!
                Logger.w("dpassword", "enter background clear shared data")
                KeyShareLiveData.get().postValue(null)
            }
        }
    }

}