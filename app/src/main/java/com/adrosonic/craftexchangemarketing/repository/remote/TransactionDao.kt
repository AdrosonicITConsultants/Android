package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchangemarketing.repository.data.request.enquiry.BuyerPayment
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TransactionDao {

    @Headers("Accept: application/json")
    @POST("enquiry/Payment")
    fun uploadPaymentDetails(@Header("Authorization") token: String,
                             @Header("Content-Type") headerValue:String,
                             @Query("payment") payment : String,
                             @Body file : MultipartBody) : Call<ResponseBody>

//    fun registerUserPhoto(@Header("Content-Type") headerValue:String,
//                          @Query("registerRequest") registerRequest : String,
//                          @Body brandLogo : MultipartBody
//    ): Call<RegisterResponse>
}