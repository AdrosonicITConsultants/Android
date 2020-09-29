package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.payment.PaymentReceiptResponse
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

    @Headers("Accept: application/json")
    @PUT("enquiry/validateAdvancePaymentFromArtisan")
    fun validateAdvancePayment(@Header("Authorization") token: String,
                             @Query("enquiryId") enquiryId : Long,
                             @Query("status") status : String ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("enquiry/getAdvancedPaymentReceipt/{enquiryId}")
    fun getAdvancePaymentReceipt(@Header("Authorization") token:String,
                                 @Query("enquiryId") enquiryId : Long) : Call<PaymentReceiptResponse>

   }
