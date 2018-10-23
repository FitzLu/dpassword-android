package com.dpass.android.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dpass.android.R

class SingleTipDialog(context: Context,
                      private val tip: String): Dialog(context, R.style.DpDialog) {

    companion object {
        fun newInstance(c: Context, t: String) = SingleTipDialog(c, t)
    }

    interface OnConfirmClickListener {
        fun onClicked()
    }

    var mOnConfirmClickListener: OnConfirmClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_single_tip)
        findViewById<TextView>(R.id.dialogTip)?.text = tip
        findViewById<View>(R.id.tvConfirm)?.setOnClickListener {
            dismiss()
            mOnConfirmClickListener?.onClicked()
        }

    }
}