package com.dpass.android.activities.main.account

import android.arch.lifecycle.Observer
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dpass.android.R
import com.dpass.android.base.BaseVCFragment
import com.dpass.android.bean.AccountState
import com.dpass.android.common.BALANCE_TICKSIZE
import com.dpass.android.live.AccountStateLiveData
import com.dpass.android.net.AccountRequest
import com.dpass.android.net.NetWorker
import com.dpass.android.net.Response
import com.dpass.android.utils.FormatUtil
import com.dpass.android.utils.Logger
import com.google.gson.GsonBuilder
import io.nebulas.nebulas.Nebulas
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.math.BigDecimal
import java.math.RoundingMode

class AccountFragment : BaseVCFragment() {

    companion object {
        fun newInstance(p0: Bundle?) = AccountFragment().also {
            if (p0 != null) it.arguments = p0
        }
    }
    override var layoutResId: Int = R.layout.fragment_account

    private var refreshLayout: SwipeRefreshLayout? = null
    private var tvBalance: TextView?  = null
    private var tvAddress: TextView?  = null
    private var ivCopy   : ImageView? = null
    private var parentView: View?     = null

    private var tickSize = 1000000000000000000.toDouble()

    override fun onCreate(savedInstanceState: Bundle?) {
        AccountStateLiveData.get().observe(this, Observer<AccountState>{t->
            if (!t?.balance.isNullOrEmpty()) {
                try {
                    val result  = BigDecimal(t!!.balance).divide(BigDecimal(tickSize), BALANCE_TICKSIZE, RoundingMode.DOWN)
                    val balance = FormatUtil.format(result.toString(), BALANCE_TICKSIZE)
                    tvBalance?.text = balance
                } catch (e: Exception) {

                }
            }else{
                tvBalance?.text = "--.--"
            }
        })
        super.onCreate(savedInstanceState)
    }

    override fun setUpViews(root: View, savedInstanceState: Bundle?) {
        with(root){
            tvBalance = findViewById(R.id.tvBalance)
            tvAddress = findViewById(R.id.tvAddress)
            ivCopy    = findViewById(R.id.ivCopy)
            parentView = findViewById(R.id.parentView)
            refreshLayout = findViewById(R.id.refreshLayout)
        }
    }

    override fun workOnViewFirstCreated(savedInstanceState: Bundle?) {
        refreshLayout?.setOnRefreshListener {
            obtainAccountState()
        }
        tvAddress?.text = Nebulas.getMyWalletAddress()
        ivCopy?.setOnClickListener {
            val text =  tvAddress?.text
            if (!text.isNullOrEmpty() && mContext != null && parentView != null) {
                try {
                    val clipboard = mContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("", text)
                    clipboard.primaryClip = clip
                    Snackbar.make(parentView!!, getString(R.string.copy_success), Snackbar.LENGTH_SHORT)
                            .show()
                }catch (e: Exception){
                    Logger.e(e.toString())
                    Snackbar.make(parentView!!, getString(R.string.copy_failed), Snackbar.LENGTH_SHORT)
                            .show()
                }
            }
        }
    }

    override fun work(savedInstanceState: Bundle?) {

    }

    private var isLoading = false
    private fun obtainAccountState(){
        if (isLoading){
            return
        }
        val address = Nebulas.getMyWalletAddress()
        if (address.isNullOrEmpty()){
            return
        }
        var params = ""
        try {
            params = GsonBuilder().create().toJson(AccountRequest(address))
        }catch (e: Exception){
            Logger.i(e.toString())
        }
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params)
        isLoading = true
        refreshLayout?.isRefreshing = true
        NetWorker.callRxApiLauncher().accountState(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Response<AccountState>>(){

                    override fun onComplete() {

                    }

                    override fun onNext(t: Response<AccountState>) {
                        isLoading = false
                        refreshLayout?.isRefreshing = false
                        AccountStateLiveData.get().postValue(t.result)
                    }

                    override fun onError(e: Throwable) {
                        refreshLayout?.isRefreshing = false
                        isLoading = false
                    }


                }).also { enqueueToComposite(it) }
    }
}