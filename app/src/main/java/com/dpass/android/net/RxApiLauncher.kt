package com.dpass.android.net

import com.dpass.android.bean.*
import com.dpass.android.common.MSG_ERROR
import com.dpass.android.utils.Logger
import io.reactivex.Observable
import io.reactivex.ObservableSource
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.Result

class RxApiLauncher(mRetrofit: Retrofit) {

    private val mApi = mRetrofit.create(Apis::class.java)

    fun nebState(): Observable<Response<Chain>> = map(mApi.nebState())

    fun gasPrice(): Observable<Response<GasPrice>> = map(mApi.gasPrice())

    fun accountState(requestBody: RequestBody):Observable<Response<AccountState>> = map(mApi.accountState(requestBody))

    fun rawtransaction(requestBody: RequestBody): Observable<Response<TxHash>> = map(mApi.rawtransaction(requestBody))

    fun transactionReceipt(requestBody: RequestBody): Observable<Response<TransactionReceipt>> = map(mApi.transactionReceipt(requestBody))

    fun call(requestBody: RequestBody): Observable<Response<Call>> = map(mApi.call(requestBody))

    private fun <T> map(observable: Observable<Result<Response<T>>>): Observable<Response<T>> {
        return observable.concatMap(io.reactivex.functions.Function<Result<Response<T>>, ObservableSource<out Response<T>>> { t ->
            if (t.isError || t.response() == null) {
                if (t.error() != null && t.error()!!.message != null) {
                    Logger.e(javaClass.simpleName, t.error()?.message?:"")
                }
                return@Function Observable.error<Response<T>>(Throwable(MSG_ERROR))
            }

            if (t.response()!!.isSuccessful) {
                return@Function Observable.just(t.response()!!.body()!!)
            } else {
                return@Function Observable.error(Throwable(MSG_ERROR))
            }
        })
    }
}