package com.v2.okcredit.api

import com.v2.okcredit.api.callback.OtpResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*


interface RestAPI {
    @GET("create/{countrycode}/{mobilenumber}")
    fun createUser(
        @Path("countrycode") code: String,
        @Path("mobilenumber") mobile: String
    ): Single<ResponseBody>

    @GET("verify/{mobilenumber}/{OTP}")
    fun verifyUser(
        @Path("mobilenumber") mobile: String,
        @Path("OTP") otp: String
    ): Single<OtpResponse>

    @FormUrlEncoded
    @POST("transact")
    fun createTransact(
        @Field("ShopId") ChopId: String,
        @Field("CustomerMobileNumber") mobile: String,
        @Field("TransactionAmount") amt: String,
        @Field("TransactionTime") time: String,
        @Field("NoteTransaction") note: String
    ): Single<ResponseBody>
}