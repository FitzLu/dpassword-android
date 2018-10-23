package com.dpass.android.stroage

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.MainThread

class KeyShareLiveData: MutableLiveData<ByteArray>() {

    companion object {

        private var holder: KeyShareLiveData? = null

        @MainThread
        fun get(): KeyShareLiveData {
            if (holder == null) {
                holder = KeyShareLiveData()
            }
            return holder!!
        }

    }

    fun isEmpty() = value == null || value!!.isEmpty()

}