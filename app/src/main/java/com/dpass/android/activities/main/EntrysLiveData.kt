package com.dpass.android.activities.main

import android.arch.lifecycle.MutableLiveData
import com.dpass.android.bean.EntryStore

class EntrysLiveData: MutableLiveData<ArrayList<EntryStore>>() {

    companion object {

        private var mInstance: EntrysLiveData? = null

        fun get(): EntrysLiveData {
            if (mInstance == null) {
                mInstance = EntrysLiveData()
            }
            return mInstance!!
        }
    }

    fun isEmpty() = value == null || value!!.isEmpty()

}