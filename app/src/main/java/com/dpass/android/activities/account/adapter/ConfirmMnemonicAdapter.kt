package com.dpass.android.activities.account.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dpass.android.R
import com.dpass.android.utils.ScreenUtil
import com.dpass.android.viewholder.ConfirmMnemonicViewHolder

class ConfirmMnemonicAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context

    var mnemonicCode: ArrayList<ConfirmMnemonicPair> = arrayListOf()
    var mOnItemClickListener: OnItemClickListener? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mContext = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ConfirmMnemonicViewHolder(mContext, parent).also {
                it.itemView.minimumWidth = ((ScreenUtil.getScreenWidth(mContext)
                        - 2 * mContext.resources.getDimension(R.dimen.default_edge_margin)) / 3).toInt()
            }

    override fun getItemCount(): Int = mnemonicCode.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ConfirmMnemonicViewHolder && position < mnemonicCode.size){
            if (mnemonicCode[position].status == 0){
                holder.textView?.setTextColor(mContext.resources.getColor(R.color.disableGray))
            }else{
                holder.textView?.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            }
            holder.textView?.text = mnemonicCode[position].code
        }
    }

    public interface OnItemClickListener {
        fun onClicked(mnemonic: String, adapterPosition: Int)
    }

}