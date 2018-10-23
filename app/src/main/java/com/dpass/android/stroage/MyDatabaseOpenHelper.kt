package com.dpass.android.stroage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.dpass.android.activities.main.EntrysLiveData
import com.dpass.android.base.BaseAppActivity
import com.dpass.android.bean.Entry
import com.dpass.android.bean.EntryStore
import com.dpass.android.utils.Logger
import com.dpass.android.utils.Perception
import com.google.gson.GsonBuilder
import io.nebulas.crypto.cipher.Cipher
import io.nebulas.crypto.keystore.Algorithm
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context): ManagedSQLiteOpenHelper(ctx, "DpasswordData", null, 1) {

    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(EntryStore.tableName, true,
                EntryStore.IDColumn to INTEGER + PRIMARY_KEY + UNIQUE,
                EntryStore.valueColumn       to TEXT,
                EntryStore.stateColumn       to TEXT,
                EntryStore.txHashColumn      to TEXT,
                EntryStore.nameColumn        to TEXT,
                EntryStore.usernameColumn    to TEXT,
                EntryStore.hashIdColumn      to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
//        db.dropTable(EntryStore.tableName, true)
//        onCreate(db)
    }
}

fun BaseAppActivity.decryptEntry(entryCipherText: String, key: ByteArray): Observable<Entry>{
    return Observable.defer{
        val cipher = Cipher(Algorithm.SCRYPT)
        val decryptEntry = cipher.decrypt(entryCipherText, key)
        val gson = GsonBuilder().create()
        val entry = gson.fromJson<Entry>(String(decryptEntry), Entry::class.java)
        Observable.just(entry)
    }
}

fun BaseAppActivity.loadLocalEntrys(context: Context){
    Perception.getSharedKeyOrLock(context).apply {
        if (isAlive){
            loadLocalEntrys(context, key)
        }
    }
}

fun BaseAppActivity.loadLocalEntrys(context: Context, key: ByteArray) {
    Observable.defer {
        val rowParser = classParser<EntryStore>()
        val entryStores = context.database.readableDatabase
                .select(EntryStore.tableName, EntryStore.IDColumn, EntryStore.valueColumn,
                        EntryStore.stateColumn, EntryStore.txHashColumn, EntryStore.nameColumn,
                        EntryStore.usernameColumn, EntryStore.hashIdColumn).exec {
                    parseList(rowParser)
                }
        val array = arrayListOf<EntryStore>()
        entryStores.forEach {
            if (it.state != EntryStore.statusSyncingDelete
                    && it.state != EntryStore.statusSyncDeleteFailed){
                array.add(it)
            }
        }
        Observable.just(array)
    }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<ArrayList<EntryStore>>(){

                override fun onComplete() {}

                override fun onNext(t: ArrayList<EntryStore>) {
                    EntrysLiveData.get().postValue(t)
                }

                override fun onError(e: Throwable) {
                    Logger.e("loadLocalEntrys", e.toString())
                }

            }).also { enqueueToComposite(it) }
}

// Access property for Context
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)