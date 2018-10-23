package com.dpass.android.net

import com.dpass.android.bean.*
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface Apis {

    @GET("/v1/user/nebstate")
    fun nebState(): Observable<Result<Response<Chain>>>

    @GET("/v1/user/getGasPrice")
    fun gasPrice(): Observable<Result<Response<GasPrice>>>

    @Headers("Content-Type: application/json")
    @POST("/v1/user/accountstate")
    fun accountState(@Body requestBody: RequestBody): Observable<Result<Response<AccountState>>>

    @Headers("Content-Type: application/json")
    @POST("/v1/user/rawtransaction")
    fun rawtransaction(@Body requestBody: RequestBody): Observable<Result<Response<TxHash>>>

    @Headers("Content-Type: application/json")
    @POST("/v1/user/getTransactionReceipt")
    fun transactionReceipt(@Body requestBody: RequestBody): Observable<Result<Response<TransactionReceipt>>>

    @Headers("Content-Type: application/json")
    @POST("/v1/user/call")
    fun call(@Body requestBody: RequestBody): Observable<Result<Response<Call>>>

}