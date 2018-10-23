package com.dpass.android.widgets

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.dpass.android.R
import com.dpass.android.utils.Logger

class TipPopupWindow(private val mContext: Context): PopupWindow() {

    private var windowsWidth  = 0
    private var windowsHeight = 0

    init {
        width  = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        contentView = LayoutInflater.from(mContext).inflate(R.layout.popup_window_tip, null, false)
        setBackgroundDrawable(ColorDrawable(0x00000000))
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        windowsWidth  = contentView.measuredWidth
        windowsHeight = contentView.measuredHeight
    }

    fun showToStartOf(view: View){
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        Logger.i("test", "${location[0]} - ${location[1]}")
        val x = location[0] - windowsWidth
        val y = location[1] - (windowsHeight - view.height) / 2f
        showAtLocation(view, Gravity.NO_GRAVITY, x, y.toInt())
    }

}