package com.dpass.android.activities.main.entrys

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.dpass.android.R
import com.dpass.android.bean.EntryStore
import com.dpass.android.utils.Logger
import com.dpass.android.viewholder.EntryViewHolder
import com.dpass.android.viewholder.FootViewHolder



class EntrysAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val typeEntry = 0
    private val typeFoot  = 1

    interface EntrysActionListener {
        fun onBindFirstEntry(x: Float, y: Float, syncImageView: ImageView)
        fun onItemClicked(entryStore: EntryStore)
        fun onCopyClicked(entryStore: EntryStore)
        fun onSyncClicked(entryStore: EntryStore, adapterPosition: Int)
    }

    private lateinit var mContext: Context

    var entrysActionListener: EntrysActionListener? = null

    var entrys = arrayListOf<EntryStore>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mContext = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when(viewType){
                typeEntry -> EntryViewHolder(mContext, parent)
                else      -> FootViewHolder(mContext, parent)
            }

    override fun getItemCount(): Int = entrys.size + 1

    override fun getItemViewType(position: Int): Int =
            if (position < itemCount - 1) typeEntry else typeFoot

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            typeEntry -> bindEntry(holder, position)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is EntryViewHolder){
            holder.ivSyncStatus?.clearAnimation()
        }
        super.onViewRecycled(holder)
    }

    private fun bindEntry(holder: RecyclerView.ViewHolder, position: Int){
        if (holder is EntryViewHolder && position < entrys.size){
            Logger.i("${javaClass.simpleName}::state=>", entrys[position].state)
            Logger.i("${javaClass.simpleName}::txHash=>", entrys[position].txHash)
            Logger.i("${javaClass.simpleName}::hashId=>", entrys[position].hashId)

            if (position == 0 && holder.ivSyncStatus != null){
                val imageCenterX = holder.ivSyncStatus.x + holder.ivSyncStatus.width / 2f
                val imageCenterY = holder.ivSyncStatus.y + holder.ivSyncStatus.height / 2f
                entrysActionListener?.onBindFirstEntry(imageCenterX, imageCenterY, holder.ivSyncStatus)
            }

            if (entrys[position].name.isNotEmpty()){
                holder.tvLogo?.text = entrys[position].name[0].toString()
            }
            holder.tvName?.text = entrys[position].name
            holder.tvUsername?.text = entrys[position].username

            holder.ivSyncStatus?.clearAnimation()
            when(entrys[position].state){
                EntryStore.statusSyncSuccess ->
                    holder.ivSyncStatus?.setImageResource(R.drawable.ic_ok)

                EntryStore.statusSyncFailed, EntryStore.statusSyncModifyFailed->
                    holder.ivSyncStatus?.setImageResource(R.drawable.ic_wrong)

                EntryStore.statusSyncing, EntryStore.statusSyncingModify -> {
                    holder.ivSyncStatus?.setImageResource(R.drawable.ic_refresh)
                    val anim = RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    anim.interpolator = LinearInterpolator()
                    anim.duration     = 1000
                    anim.repeatMode   = Animation.RESTART
                    anim.repeatCount  = Animation.INFINITE
                    holder.ivSyncStatus?.animation = anim
                    anim.start()
                }
                else                         ->
                    holder.ivSyncStatus?.setImageResource(R.drawable.ic_sync)
            }
            holder.itemView.setOnClickListener {
                try {
                    entrysActionListener?.onItemClicked(entrys[position])
                }catch (e: ArrayIndexOutOfBoundsException){

                }
            }
            holder.ivCopy?.setOnClickListener {
                try {
                    entrysActionListener?.onCopyClicked(entrys[position])
                }catch (e: ArrayIndexOutOfBoundsException){

                }
            }
            holder.ivSyncStatus?.setOnClickListener {
                try {
                    entrysActionListener?.onSyncClicked(entrys[position], holder.adapterPosition)
                }catch (e: ArrayIndexOutOfBoundsException){

                }
            }
        }
    }
}