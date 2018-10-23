package com.dpass.android.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dpass.android.R

class TipAlertDialog(context: Context,
                     private val tip: String,
                     private val color: Int?): Dialog(context, R.style.DpDialog) {

    interface OnConfirmClickListener {
        fun onClicked()
    }

    var mOnConfirmClickListener: OnConfirmClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_tip_alert)
        findViewById<TextView>(R.id.dialogTip)?.text = tip
        if (color != null){
            findViewById<TextView>(R.id.dialogTip)?.setTextColor(color)
        }
        findViewById<View>(R.id.tvCancel)?.setOnClickListener {
            dismiss()
        }
        findViewById<View>(R.id.tvConfirm)?.setOnClickListener {
            dismiss()
            mOnConfirmClickListener?.onClicked()
        }

    }

}