package com.dpass.android.widgets

import android.text.Editable
import android.text.TextWatcher

abstract class SingleTextChangeWatcher: TextWatcher {

    abstract fun textChanged(s: CharSequence?, start: Int, before: Int, count: Int)

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        textChanged(s, start, before, count)
    }
}