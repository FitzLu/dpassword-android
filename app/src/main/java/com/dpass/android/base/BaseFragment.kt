package com.dpass.android.base

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.widget.Toast
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment: Fragment(), LifecycleOwner {

    protected var mContext: Context? = null

    private var mCompositeDisposable: CompositeDisposable? = null

    fun enqueueToComposite(@NonNull d: Disposable){
        mCompositeDisposable?.add(d)
    }

    fun dequeueFromComposite(@NonNull d: Disposable){
        mCompositeDisposable?.remove(d)
    }

    fun broadCast(broadCast: String){
        if (mContext == null){
            return
        }
        mContext!!.sendBroadcast(Intent(broadCast))
    }

    fun broadCast(intent: Intent){
        if (mContext == null){
            return
        }
        mContext!!.sendBroadcast(intent)
    }

    fun showToast(message: String?){
        if (mContext == null || message.isNullOrEmpty()){
            return
        }
        Toast.makeText(mContext!!, message, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDestroy() {
        mCompositeDisposable?.clear()
        super.onDestroy()
    }
}