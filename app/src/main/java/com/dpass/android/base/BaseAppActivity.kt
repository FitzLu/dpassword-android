package com.dpass.android.base

import android.arch.lifecycle.LifecycleOwner
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.dpass.android.R
import com.dpass.android.utils.AppForegroundStateManager
import com.dpass.android.utils.Logger
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseAppActivity : AppCompatActivity(), LifecycleOwner {

    abstract var mTag       : String

    abstract var layoutResId: Int

    abstract var hasToolbar : Boolean

    private var mCompositeDisposable: CompositeDisposable? = null

    /**
     * toolbar related
     * */
    private var mToolbar              : Toolbar? = null
    private var mToolbarProgress      : ProgressBar? = null
    private var mToolbarTitle         : TextView?  = null
    private var mToolbarTitleRightIcon: ImageView? = null
    private var mToolbarNavIcon       : ImageView? = null
    private var mToolbarRightTextView : TextView?  = null
    private var mToolbarRightIcon     : ImageView? = null
    private var mToolbarSearchView    : SearchView? = null

    private var mTitle = ""

    /**
     * broad cast receiver instance
     * Will be auto unregister when destroy if created
     * @see registersBroadCast
     * @see onReceiveBroadCast
     * @see onDestroy
     * */
    private var mBroadcastReceiver: BroadcastReceiver? = null

    abstract fun setUpViews(savedInstanceState: Bundle?)

    abstract fun work(savedInstanceState: Bundle?)

    open fun storeForegroundState() = true

    fun enqueueToComposite(@NonNull d: Disposable){
        mCompositeDisposable?.add(d)
    }

    fun dequeueFromComposite(@NonNull d: Disposable){
        mCompositeDisposable?.remove(d)
    }

    /**
     * @see onReceiveBroadCast
     * @see mBroadcastReceiver
     * @see SelfBroadCast
     * */
    protected open fun registersBroadCast(vararg intentFilter: String){
        try{
            mBroadcastReceiver = SelfBroadCast()
            registerReceiver(mBroadcastReceiver, IntentFilter().also {
                for (s in intentFilter) {
                    it.addAction(s)
                }
            })
        } catch (e: Exception) {
            Logger.e(e.toString())
        }
    }

    /**
     * @see SelfBroadCast.onReceive
     * */
    @CallSuper
    protected open fun onReceiveBroadCast(context: Context?, intent: Intent?){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCompositeDisposable = CompositeDisposable()
        setContentView(layoutResId)
        if (hasToolbar) initToolbar()
        setUpViews(savedInstanceState)
        work(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (storeForegroundState()) {
            AppForegroundStateManager.getInstance().onActivityVisible(this)
        }
    }

    override fun onStop() {
        if (storeForegroundState()) {
            AppForegroundStateManager.getInstance().onActivityNotVisible(this)
        }
        super.onStop()
    }

    override fun onDestroy() {
        mCompositeDisposable?.clear()
        if (mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver!!)
        }
        super.onDestroy()
    }

    /**
     * Config toolbar，layout need includes
     * @see hasToolbar
     * @see R.layout.custom_toolbar
     * */
    private fun initToolbar(){
        mToolbar = findViewById(R.id.toolbar)
        if (mToolbar == null){
            throw RuntimeException("Layout must include a toolbar and it`s id must be toolbar when hasToolbar returns true")
        }
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mToolbarProgress        = findViewById(R.id.toolbarProgress)
        mToolbarTitle           = findViewById(R.id.toolbarTitle)
        mToolbarTitleRightIcon  = findViewById(R.id.toolbarTitleRightIcon)
        mToolbarNavIcon         = findViewById(R.id.toolbarNavIcon)
        mToolbarRightTextView   = findViewById(R.id.toolbarRightTextView)
        mToolbarSearchView      = findViewById(R.id.toolbarSearchView)
        mToolbarRightIcon       = findViewById(R.id.toolbarRightIcon)

        //Default nav icon style
        //Navigation back
        mToolbarNavIcon?.visibility = View.VISIBLE
        mToolbarNavIcon?.setImageResource(R.drawable.ic_chevron_left)
        mToolbarNavIcon?.setOnClickListener { finish() }
    }

    //设置 toolbar 背景颜色
    protected fun setToolbarBackgroundColor(color: Int){
        mToolbar?.setBackgroundColor(color)
    }

    /** toolbar title config **/

    protected fun setToolbarTitle(text: String){
        mToolbarTitle?.visibility = View.VISIBLE
        mToolbarTitle?.text = text
        mTitle = text
    }

    protected fun showTitle(){
        mToolbarTitle?.visibility = View.VISIBLE
    }

    protected fun hideTitle(){
        mToolbarTitle?.visibility = View.GONE
    }

    /** toolbar nav icon config **/

    protected fun setNavIconImageResource(resId: Int){
        mToolbarNavIcon?.setImageResource(resId)
    }

    protected fun hideNavIcon(){
        mToolbarNavIcon?.visibility = View.GONE
    }

    protected fun setNavIconOnClickedListener(listener: View.OnClickListener){
        mToolbarNavIcon?.setOnClickListener(listener)
    }

    /** toolbar right text view config **/
    protected fun enableToolbarRightTextView(enable: Boolean){
        mToolbarRightTextView?.visibility = if (enable) View.VISIBLE else View.GONE
    }

    protected fun setToolbarRightText(text: String){
        mToolbarRightTextView?.text = text
    }

    protected fun setToolBarRightTextClickListener(listener: View.OnClickListener) {
        mToolbarRightTextView?.setOnClickListener(listener)
    }

    /** toolbar right icon config **/
    protected fun enableToolbarRightIcon(enable: Boolean){
        mToolbarRightIcon?.visibility = if (enable) View.VISIBLE else View.GONE
    }

    protected fun setToolbarRightIcon(resId: Int){
        mToolbarRightIcon?.setImageResource(resId)
    }

    protected fun setToolbarRightIconClickListener(listener: View.OnClickListener) {
        mToolbarRightIcon?.setOnClickListener(listener)
    }

    /** toolbar search config **/
    protected fun enableToolbarSearchView(enable: Boolean){
        mToolbarSearchView?.visibility = if (enable) View.VISIBLE else View.GONE
    }

    protected fun setSearchViewQueryListener(listener: SearchView.OnQueryTextListener){
        mToolbarSearchView?.setOnQueryTextListener(listener)
    }

    protected fun setSearchViewClickListener(listener: View.OnClickListener){
        mToolbarSearchView?.setOnSearchClickListener(listener)
    }

    protected fun setSearchViewCloseListener(listener: SearchView.OnCloseListener){
        mToolbarSearchView?.setOnCloseListener(listener)
    }

    /** broad cast **/
    protected fun broadCast(broadCast: String){
        sendBroadcast(Intent(broadCast))
    }

    protected fun broadCast(broadCast: Intent){
        sendBroadcast(broadCast)
    }

    protected fun showToast(message: String?){
        if (!message.isNullOrEmpty()){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    inner class SelfBroadCast: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            onReceiveBroadCast(context, intent)
        }
    }
}