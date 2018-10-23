package com.dpass.android.activities.lock

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.dpass.android.R
import com.dpass.android.base.BaseAppActivity
import com.dpass.android.stroage.KeyShareLiveData
import com.dpass.android.widgets.PinView
import com.dpass.android.widgets.SingleTextChangeWatcher
import io.nebulas.exception.KeyDecryptException
import io.nebulas.exception.PassphraseException
import io.nebulas.nebulas.Nebulas
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class ScreenLockActivity: BaseAppActivity() {

    override var mTag: String = javaClass.simpleName
    override var layoutResId: Int = R.layout.activity_screen_lock
    override var hasToolbar: Boolean = false

    private var llPinView: LinearLayout? = null
    private var pinView: PinView? = null
    private var progress: ProgressBar? = null
    private var tip: TextView? = null
    private var passphrase = ""

    override fun setUpViews(savedInstanceState: Bundle?) {
        pinView = findViewById(R.id.pinView)
        progress = findViewById(R.id.progress)
        llPinView = findViewById(R.id.llPinView)
        tip = findViewById(R.id.tip)
        pinView?.addTextChangedListener(object : SingleTextChangeWatcher(){
            override fun textChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tip?.text = ""
                tip?.setTextColor(resources.getColor(R.color.colorPrimary))
                passphrase = s?.toString()?:""
                if (passphrase.length == 6){
                    loadPrivateKey(passphrase)
                }
            }
        })
    }

    override fun work(savedInstanceState: Bundle?) {
        launchInput()
    }

    override fun onBackPressed() {}

    private fun launchInput(){
        llPinView?.visibility = View.VISIBLE
        progress?.visibility = View.GONE
    }

    private var isLoading = false
    private fun loadPrivateKey(ph: String){
        if (isLoading) return
        isLoading = true
        llPinView?.visibility = View.GONE
        progress?.visibility = View.VISIBLE
        newLoadTask(ph).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<ByteArray>(){

                    override fun onComplete() {
                    }

                    override fun onNext(t: ByteArray) {
                        KeyShareLiveData.get().postValue(t)
                        isLoading = false
                        finish()
                    }

                    override fun onError(e: Throwable) {
                        KeyShareLiveData.get().postValue(null)
                        pinView?.setText("")
                        isLoading = false
                        tip?.setTextColor(resources.getColor(R.color.errorColor))
                        if (e is PassphraseException || e is KeyDecryptException){
                            tip?.text = getString(R.string.password_error)
                        }else{
                            tip?.text = getString(R.string.unlock_failed)
                        }
                        launchInput()
                    }

                }).also { enqueueToComposite(it) }
    }

    private fun newLoadTask(passPhrase: String): Observable<ByteArray> {
        return Observable.defer {
            val privateKey = Nebulas.getMyPrivateKey(passPhrase)
            Observable.just(privateKey)
        }
    }
}