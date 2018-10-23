package com.dpass.android.activities.entry

import android.content.*
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.dpass.android.R
import com.dpass.android.base.BaseAppActivity
import com.dpass.android.bean.Entry
import com.dpass.android.bean.EntryStore
import com.dpass.android.common.BroadCastConstants
import com.dpass.android.common.BundleConstants
import com.dpass.android.dialogs.TipAlertDialog
import com.dpass.android.live.AccountStateLiveData
import com.dpass.android.stroage.database
import com.dpass.android.stroage.decryptEntry
import com.dpass.android.stroage.loadLocalEntrys
import com.dpass.android.utils.Logger
import com.dpass.android.utils.Perception
import com.dpass.android.utils.SupportActivityUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_entry_detail.*

class EntryDetailActivity: BaseAppActivity() {

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_entry_detail
    override var hasToolbar: Boolean = false

    private var eyeOpen = false
    private var entryStore: EntryStore? = null
    private var entry     : Entry? = null

    override fun setUpViews(savedInstanceState: Bundle?) {
        tvDelete?.setOnClickListener {
            if (entryStore != null){
                if (Perception.checkBalanceEnough(AccountStateLiveData.get().value?.balance)) {
                    TipAlertDialog(this@EntryDetailActivity, getString(R.string.ensure_whether_delete), null)
                            .also { it.mOnConfirmClickListener = object : TipAlertDialog.OnConfirmClickListener{
                                override fun onClicked() {
                                    val broad = Intent(BroadCastConstants.DELETE_ENTRY)
                                    broad.putExtra(BundleConstants.DATA, entryStore!!)
                                    broadCast(broad)
                                    val value = ContentValues()
                                    value.put(EntryStore.stateColumn, EntryStore.statusSyncingDelete)
                                    database.writableDatabase.use {
                                        it.update(EntryStore.tableName, value, "id = ${entryStore!!.id}", null)
                                    }
                                    loadLocalEntrys(this@EntryDetailActivity)
                                    showToast(getString(R.string.delete_success))
                                    finish()
                                }
                            } }.show()
                }else{
                    TipAlertDialog(this, getString(R.string.insufficient_balance), null).also {
                        it.mOnConfirmClickListener = object : TipAlertDialog.OnConfirmClickListener{
                            override fun onClicked() {
                                it.dismiss()
                            }
                        }
                    }.show()
                }
            }
        }
        tvGoBack?.setOnClickListener { finish() }
        tvEdit?.setOnClickListener {
            SupportActivityUtil.jumpByIntent(this@EntryDetailActivity,
                    Intent(this@EntryDetailActivity, EditEntryActivity::class.java).also {
                        it.putExtra(BundleConstants.DATA, Pair(entryStore, entry))
                    })
        }
        ivEye?.setOnClickListener {
            if (eyeOpen){
                ivEye?.setImageResource(R.drawable.ic_eye_close)
                edtPassword?.transformationMethod = PasswordTransformationMethod.getInstance()
                eyeOpen = false
            }else{
                ivEye?.setImageResource(R.drawable.ic_eye_open)
                edtPassword?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                eyeOpen = true
            }
        }
        ivEye?.setImageResource(R.drawable.ic_eye_close)
        tvCopy?.setOnClickListener {
            val text =  edtPassword?.text
            if (!text.isNullOrEmpty()) {
                try {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(tvName?.text?:"", text)
                    clipboard.primaryClip = clip
                    showToast(getString(R.string.copy_success))
                }catch (e: Exception){
                    Logger.e(e.toString())
                    showToast(getString(R.string.copy_failed))
                }
            }
        }
        edtPassword?.transformationMethod = PasswordTransformationMethod.getInstance()
        eyeOpen = false
    }

    override fun work(savedInstanceState: Bundle?) {
        registersBroadCast(BroadCastConstants.COMPLETE_EDIT_ENTRY)
        try {
            entryStore = intent.getSerializableExtra(BundleConstants.DATA) as EntryStore
            if (!entryStore?.value.isNullOrEmpty()){
                Perception.getSharedKeyOrLock(this@EntryDetailActivity).apply {
                    if (isAlive){
                        decryptEntry(entryStore!!.value, key)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(object : DisposableObserver<Entry>(){
                                    override fun onComplete() {
                                    }

                                    override fun onNext(t: Entry) {
                                        entry = t
                                        if (entry!!.name.isNotEmpty()){
                                            tvLogo?.text = entry!!.name[0].toString()
                                        }
                                        tvName?.text = entry?.name
                                        edtUrl?.text = entry?.url
                                        edtUsername?.text = entry?.username
                                        edtPassword?.text = entry?.password
                                    }

                                    override fun onError(e: Throwable) {
                                    }

                                })
                    }
                }

            }

        }catch (e: Exception){

        }
    }

    override fun onReceiveBroadCast(context: Context?, intent: Intent?) {
        super.onReceiveBroadCast(context, intent)
        when(intent?.action){
            BroadCastConstants.COMPLETE_EDIT_ENTRY -> finish()
        }
    }

}