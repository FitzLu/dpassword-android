package com.dpass.android.bean

import java.io.Serializable

data class EntryStore(var id      : Int,
                      var value   : String,
                      var state   : String,
                      var txHash  : String,
                      var name    : String,
                      var username: String,
                      var hashId  : String): Serializable {

    companion object {

        const val tableName       = "EntryStore"
        const val IDColumn        = "id"
        const val valueColumn     = "value"
        const val stateColumn     = "state"
        const val txHashColumn    = "txHash"
        const val nameColumn      = "name"
        const val usernameColumn  = "username"
        const val hashIdColumn    = "hashId"

        const val statusLocal                   = "local"                   //本地
        const val statusSyncing                 = "syncing"                 //同步中
        const val statusModify                  = "modify"                  //修改
        const val statusSyncingModify           = "syncingmodify"           //同步修改中
        const val statusSyncModifyFailed        = "syncmodifyfailed"        //同步修改失败
        const val statusSyncSuccess             = "synced"                  //同步成功
        const val statusSyncFailed              = "syncfailed"              //同步失败
        const val statusSyncingDelete           = "syncdelete"              //同步删除
        const val statusSyncDeleteFailed        = "syncdeletefailed"        //同步删除失败

    }
}
