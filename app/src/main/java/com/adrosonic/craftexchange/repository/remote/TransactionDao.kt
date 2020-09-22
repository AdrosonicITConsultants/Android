package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TransactionDao {

    @Headers("Accept: application/json")
    @POST("enquiry/Payment")
    fun uploadPaymentDetails(@Header("Content-Type") headerValue:String,
                             @Header("Authorization") token:String,
                             @Query("payment") payment : BuyerPayment,
                             @Body file : MultipartBody) : Call<ResponseBody>

//    fun registerUserPhoto(@Header("Content-Type") headerValue:String,
//                          @Query("registerRequest") registerRequest : String,
//                          @Body brandLogo : MultipartBody
//    ): Call<RegisterResponse>
}