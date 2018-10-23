package com.dpass.android.viewholder

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.dpass.android.R
import com.dpass.android.base.BaseViewHolder

class MnemonicViewHolder(context: Context, parent: ViewGroup)
    : BaseViewHolder(context, R.layout.view_holder_mnemonic, parent) {

    val textView: TextView? = itemView.findViewById(R.id.textView)

}