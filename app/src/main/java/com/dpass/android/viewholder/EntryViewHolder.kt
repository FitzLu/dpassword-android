package com.dpass.android.viewholder

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dpass.android.R
import com.dpass.android.base.BaseViewHolder

class EntryViewHolder(context: Context, parent: ViewGroup)
    : BaseViewHolder(context, R.layout.view_holder_entry, parent) {

    val tvLogo: TextView? = itemView.findViewById(R.id.tvLogo)
    val tvName: TextView? = itemView.findViewById(R.id.tvName)
    val tvUsername: TextView? = itemView.findViewById(R.id.tvUsername)
    val ivCopy: ImageView? = itemView.findViewById(R.id.ivCopy)
    val ivSyncStatus: ImageView? = itemView.findViewById(R.id.ivSyncStatus)

}