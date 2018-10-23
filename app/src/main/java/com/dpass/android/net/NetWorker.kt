package com.dpass.android.net

import android.content.Context
import com.dpass.android.net.factory.GsonFactory
import com.dpass.android.net.factory.HttpClientFactory
import com.dpass.android.net.factory.RetrofitFactory
import com.dpass.android.utils.Logger
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object NetWorker {

    private const val mTag = "NetWorker"

    private lateinit var mGson        : Gson
    private lateinit var mRetrofit    : Retrofit
    private lateinit var mOkHttpClient: OkHttpClient

    private val mainNet = "https://mainnet.nebulas.io"
    private val testNet = "http://47.97.220.86:18685"

    @Volatile
    private var mIsInInitialized = false

    private lateinit var mRxApiLauncher: RxApiLauncher

    fun initialize(context: Context){
        if (mIsInInitialized){
            Logger.w(mTag, "NetWorker has been initialized, NetWorker.initialize(...) should" +
                    "only called 1 single time to avoid memory leak")
        }else{
            mIsInInitialized = true
        }
        mGson         = GsonFactory.create()
        mOkHttpClient = HttpClientFactory.create(null)
        mRetrofit     = RetrofitFactory.create(mainNet, mGson, mOkHttpClient)

        mRxApiLauncher = RxApiLauncher(mRetrofit)
    }

    fun callRxApiLauncher() = mRxApiLauncher

    fun getGson()           = mGson

    fun getHttpClient()     = mOkHttpClient

}