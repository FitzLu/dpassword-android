package com.dpass.android.activities.account.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dpass.android.R
import com.dpass.android.utils.Logger
import com.dpass.android.utils.ScreenUtil
import com.dpass.android.viewholder.MnemonicViewHolder

class MnemonicAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    var mnemonicCode: ArrayList<MutablePair<String, Boolean>> = arrayListOf()
    var mOnItemClickListener: OnItemClickListener? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mContext = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            MnemonicViewHolder(mContext, parent).also {
                it.itemView.minimumWidth = ((ScreenUtil.getScreenWidth(mContext)
                        - 2 * mContext.resources.getDimension(R.dimen.default_edge_margin)) / 3).toInt()
            }

    override fun getItemCount(): Int = mnemonicCode.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MnemonicViewHolder && position < mnemonicCode.size){
            if (mnemonicCode[position].second){
                holder.textView?.setTextColor(mContext.resources.getColor(R.color.primaryTextColor))
                holder.textView?.setBackgroundResource(R.drawable.primary_arc_button_light)
            }else{
                holder.textView?.setTextColor(mContext.resources.getColor(R.color.disableGray))
                holder.textView?.setBackgroundResource(R.drawable.primary_arc_border)
            }
            holder.textView?.text = mnemonicCode[position].first
            holder.itemView.setOnClickListener {
                try {
                    if (mnemonicCode[position].second) {
                        mOnItemClickListener?.onClicked(mnemonicCode[position].first, holder.adapterPosition)
                    }
                }catch (e: IndexOutOfBoundsException){
                    Logger.e(javaClass.simpleName, "$e")
                }
            }
        }
    }

    fun allSelected(): Boolean{
        var result = true
        mnemonicCode.forEach {
            result = result && !it.second
        }
        return result
    }

    public interface OnItemClickListener {
        fun onClicked(mnemonic: String, adapterPosition: Int)
    }

}