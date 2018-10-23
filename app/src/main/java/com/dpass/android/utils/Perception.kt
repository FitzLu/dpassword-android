package com.dpass.android.utils

import android.content.Context
import android.content.Intent
import com.dpass.android.activities.lock.ScreenLockActivity
import com.dpass.android.common.BALANCE_TICKSIZE
import com.dpass.android.common.MIN_BALANCE
import com.dpass.android.stroage.KeyShareLiveData
import java.math.BigDecimal
import java.math.RoundingMode

object Perception {

    fun getSharedKeyOrLock(context: Context): SharedKeyLiveHolder{
        val key   = KeyShareLiveData.get().value
        val alive: Boolean
        if (key == null || key.isEmpty()){
            alive = false
            SupportActivityUtil.jumpByIntent(context,
                    Intent(context, ScreenLockActivity::class.java))
        }else{
            alive = true
        }
        return SharedKeyLiveHolder(alive, key)
    }

    fun checkBalanceEnough(balance: String?): Boolean{
        if (balance.isNullOrEmpty()){
            return false
        }
        try{
            val result  = BigDecimal(balance).divide(BigDecimal(1000000000000000000.toDouble()), BALANCE_TICKSIZE, RoundingMode.DOWN)
            val balanceDouble = FormatUtil.format(result.toString(), BALANCE_TICKSIZE).toDouble()
            return balanceDouble >= MIN_BALANCE
        }catch (e: Exception){
            Logger.e(e.toString())
        }

        return false
    }

}