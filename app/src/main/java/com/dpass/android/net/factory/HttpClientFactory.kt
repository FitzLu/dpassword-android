package com.dpass.android.net.factory

import com.dpass.android.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object HttpClientFactory {

    fun create(extraParams: HashMap<String, String>?) : OkHttpClient {
        val paramsInterceptor = Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = chain.request().url()

            val url = originalHttpUrl.newBuilder().build()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder().url(url)

            if (extraParams != null && extraParams.isNotEmpty()){
                extraParams.forEach {
                    requestBuilder.addHeader(it.key, it.value)
                }
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = if (BuildConfig.OPEN_LOG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(paramsInterceptor)
                .addInterceptor(logInterceptor)
                .build()
    }

}