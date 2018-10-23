package com.dpass.android.live

import android.arch.lifecycle.MutableLiveData
import com.dpass.android.bean.AccountState

class AccountStateLiveData: MutableLiveData<AccountState>() {

    companion object {
        private var holder: AccountStateLiveData? = null

        fun get(): AccountStateLiveData{
            if (holder == null){
                holder = AccountStateLiveData()
            }
            return holder!!
        }

    }

    fun addNonce(){
        val lastState = value
        if (lastState != null) {
            lastState.nonce += 1
            postValue(lastState)
        }
    }

}