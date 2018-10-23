package com.dpass.android.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

open class BaseViewHolder(context: Context, layoutResId : Int, parent: ViewGroup)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(layoutResId, parent, false))