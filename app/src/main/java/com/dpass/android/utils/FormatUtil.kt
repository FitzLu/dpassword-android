package com.dpass.android.utils

import java.math.BigDecimal
import java.text.DecimalFormat

object FormatUtil {

    fun format(num: String, scale: Int): String{
        var numcopy = num
        if (numcopy.contains(",")){
            numcopy = numcopy.replace(",", ".")
        }
        var result = ""

        try {
            val df = DecimalFormat()

            val bd = BigDecimal(numcopy.toDouble())

            result = if (scale <= 0){
                df.apply { applyPattern("###0") }.format(bd)
            }else {
                val pattern = StringBuilder("###0.")
                for (index in 0 until scale) {
                    pattern.append("0")
                }

                df.apply { applyPattern(pattern.toString()) }.format(bd)
            }
        }catch (e: Exception){

        }

        return result
    }

}