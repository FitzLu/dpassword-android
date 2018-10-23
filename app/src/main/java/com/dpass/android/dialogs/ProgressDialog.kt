package com.dpass.android.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import com.dpass.android.R

class ProgressDialog(context: Context): Dialog(context, R.style.DpDialog) {

    private var mProgress: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_password)
        mProgress = findViewById(R.id.dialogProgress)
    }

}