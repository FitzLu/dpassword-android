package com.dpass.android.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dpass.android.common.BroadCastConstants

abstract class BaseAccountActivity : BaseAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registersBroadCast(BroadCastConstants.COMPLETE_CREATE_ACCOUNT)
    }

    override fun onReceiveBroadCast(context: Context?, intent: Intent?) {
        super.onReceiveBroadCast(context, intent)
        when(intent?.action) {
            BroadCastConstants.COMPLETE_CREATE_ACCOUNT -> finish()
        }
    }
}