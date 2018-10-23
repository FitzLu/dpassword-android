package com.dpass.android.bean

import java.io.Serializable

data class Entry(var id         : Int = 0,
                 var version    : Int = 1,
                 var name       : String,
                 var status     : Int = 0,
                 var url        : String,
                 var username   : String,
                 var password   : String,
                 var address    : String,
                 val updateTime : String,
                 var createTime : String): Serializable {

    companion object{

        const val statusDefault     = 0

    }


}
