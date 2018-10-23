package com.dpass.android.activities.entry

import android.content.ContentValues
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatEditText
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ImageView
import com.dpass.android.R
import com.dpass.android.base.BaseAppActivity
import com.dpass.android.bean.Entry
import com.dpass.android.bean.EntryStore
import com.dpass.android.common.BroadCastConstants
import com.dpass.android.common.BundleConstants
import com.dpass.android.common.EMPTY
import com.dpass.android.dialogs.ProgressDialog
import com.dpass.android.stroage.database
import com.dpass.android.utils.Logger
import com.dpass.android.utils.Perception
import com.dpass.android.utils.SupportActivityUtil
import com.dpass.android.widgets.SingleTextChangeWatcher
import com.google.gson.GsonBuilder
import com.subgraph.orchid.encoders.Hex
import io.nebulas.crypto.cipher.Cipher
import io.nebulas.crypto.hash.Hash
import io.nebulas.crypto.keystore.Algorithm
import io.nebulas.nebulas.Nebulas
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_entry.*

class EditEntryActivity : BaseAppActivity(){

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_edit_entry
    override var hasToolbar: Boolean = false

    private var eyeOpen = false

    private var name     = ""
    private var url      = ""
    private var username = ""
    private var password = ""

    private var entryStore: EntryStore? = null
    private var entry     : Entry?      = null

    override fun setUpViews(savedInstanceState: Bundle?) {
        findViewById<View>(R.id.tvCancel)?.setOnClickListener {
            finish()
        }
        findViewById<View>(R.id.tvSave)?.setOnClickListener {
            if (name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                Perception.getSharedKeyOrLock(this@EditEntryActivity).apply {
                    if (isAlive){
                        save(key)
                    }
                }
            }
        }
        findViewById<ImageView>(R.id.ivEye)?.setOnClickListener {
            if (eyeOpen){
                findViewById<ImageView>(R.id.ivEye)?.setImageResource(R.drawable.ic_eye_close)
                findViewById<AppCompatEditText>(R.id.edtPassword)?.transformationMethod = PasswordTransformationMethod.getInstance()
                findViewById<AppCompatEditText>(R.id.edtPassword)?.setSelection(password.length)
                eyeOpen = false
            }else{
                findViewById<ImageView>(R.id.ivEye)?.setImageResource(R.drawable.ic_eye_open)
                findViewById<AppCompatEditText>(R.id.edtPassword)?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                findViewById<AppCompatEditText>(R.id.edtPassword)?.setSelection(password.length)
                eyeOpen = true
            }
        }
        findViewById<View>(R.id.tvGenerate)?.setOnClickListener {
            SupportActivityUtil.jumpActivity(this@EditEntryActivity,
                    GenerateEntryActivity::class.java)
        }
        findViewById<AppCompatEditText>(R.id.edtName)
                ?.addTextChangedListener(object : SingleTextChangeWatcher(){
                    override fun textChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        name = s?.toString()?: EMPTY
                    }
                })
        findViewById<AppCompatEditText>(R.id.edtUrl)
                ?.addTextChangedListener(object : SingleTextChangeWatcher(){
                    override fun textChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        url = s?.toString()?: EMPTY
                    }
                })
        findViewById<AppCompatEditText>(R.id.edtUsername)
                ?.addTextChangedListener(object : SingleTextChangeWatcher(){
                    override fun textChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        username = s?.toString()?: EMPTY
                    }
                })
        findViewById<AppCompatEditText>(R.id.edtPassword)
                ?.addTextChangedListener(object : SingleTextChangeWatcher(){
                    override fun textChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        password = s?.toString()?: EMPTY
                    }
                })
    }

    override fun work(savedInstanceState: Bundle?) {
        try{
            val pair = intent.getSerializableExtra(BundleConstants.DATA) as Pair<EntryStore, Entry>
            entryStore = pair.first
            entry      = pair.second
            edtName?.setText(entry?.name)
            edtUrl?.setText(entry?.url)
            edtUsername?.setText(entry?.username)
            edtPassword?.setText(entry?.password)
        }catch (e: Exception){

        }
    }

    private val mPasswordDialog: ProgressDialog by lazy { ProgressDialog(this) }

    private fun launchProgressDialog(){
        if (mPasswordDialog.isShowing){
            mPasswordDialog.dismiss()
        }
        mPasswordDialog.show()
    }

    private var isSaving = false
    private fun save(key: ByteArray){
        if (isSaving) return
        isSaving = true
        launchProgressDialog()
        storeEntry(key).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Boolean>(){
                    override fun onComplete() {}

                    override fun onNext(t: Boolean) {
                        isSaving = false
                        mPasswordDialog.dismiss()
                        broadCast(BroadCastConstants.COMPLETE_EDIT_ENTRY)
                        showToast(getString(R.string.save_success))
                        findViewById<View>(R.id.parentView)?.postDelayed({
                            try {
                                if (!isFinishing) {
                                    finish()
                                }
                            }catch (e: Exception){
                                Logger.e(e.toString())
                            }
                        }, 200)
                    }

                    override fun onError(e: Throwable) {
                        isSaving = false
                        mPasswordDialog.dismiss()
                        mPasswordDialog.dismiss()
                        Logger.e(e.toString())
                        Snackbar.make(findViewById<View>(R.id.parentView), getString(R.string.save_failed), Snackbar.LENGTH_INDEFINITE).show()
                    }

                }).also { enqueueToComposite(it) }
    }

    private fun storeEntry(key: ByteArray): Observable<Boolean>{
        return Observable.defer {
            val time = System.currentTimeMillis().toString()
            val entry= Entry(
                    id          = 0,
                    version     = 1,
                    name        = name,
                    status      = Entry.statusDefault,
                    url         = url,
                    username    = username,
                    password    = password,
                    address     = Nebulas.getMyWalletAddress(),
                    updateTime  = time,
                    createTime  = time)

            val gson = GsonBuilder().create()
            val cipher = Cipher(Algorithm.SCRYPT)
            val entryJson = gson.toJson(entry)
            val encryptEntry = cipher.encrypt(entryJson.toByteArray(), key)
            val encryptJson  = gson.toJson(encryptEntry)

            val values = ContentValues()
            values.put(EntryStore.valueColumn, encryptJson)
            values.put(EntryStore.txHashColumn, "")
            values.put(EntryStore.nameColumn, name)
            values.put(EntryStore.usernameColumn, username)

            if (entryStore?.value != null && entryStore!!.id > 0){
                //update
                values.put(EntryStore.stateColumn, EntryStore.statusModify)
                database.writableDatabase.update(EntryStore.tableName, values, "id = ${entryStore!!.id}", null)
            }else{
                //insert
                values.put(EntryStore.stateColumn, EntryStore.statusLocal)
                val hash = String(Hex.encode(Hash.Sha3256(encryptJson.toByteArray())))
                values.put(EntryStore.hashIdColumn, hash)
                database.writableDatabase.insert(EntryStore.tableName, null, values)
            }
            Observable.just(true)
        }
    }

}