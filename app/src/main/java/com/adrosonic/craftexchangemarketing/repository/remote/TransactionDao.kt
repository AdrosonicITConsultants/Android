package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchange.repository.data.response.taxInv.FinalPayDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchangemarketing.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.PaymentReceiptResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.transaction.SingleTransactionResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.transaction.TransactionResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.transaction.TransactionStatusData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TransactionDao {

    @Headers("Accept: application/json")
    @POST("api/enquiry/submitDeliveryChallan")
    fun uploadDeliveryChallan(@Header("Authorization") token: String,
                              @Header("Content-Type") headerValue:String,
                              @Query("enquiryId") enquiryId : Long,
                              @Query("orderDispatchDate") orderDispatchDate : String,
                              @Query("ETA") ETA : String,
                              @Body file : MultipartBody) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("api/enquiry/submitDeliveryChallan")
    fun uploadDeliveryChallanNoEta(@Header("Authorization") token: String,
                                   @Header("Content-Type") headerValue:String,
                                   @Query("enquiryId") enquiryId : Long,
                                   @Query("orderDispatchDate") orderDispatchDate : String,
                                   @Body file : MultipartBody) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("api/enquiry/Payment")
    fun uploadPaymentDetails(@Header("Authorization") token: String,
                             @Header("Content-Type") headerValue:String,
                             @Query("payment") payment : String,
                             @Body file : MultipartBody) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @PUT("api/enquiry/validateAdvancePaymentFromArtisan")
    fun validateAdvancePayment(@Header("Authorization") token: String,
                               @Query("enquiryId") enquiryId : Long,
                               @Query("status") status : String ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @PUT("api/enquiry/validateFinalPaymentFromArtisan")
    fun validateFinalPayment(@Header("Authorization") token: String,
                             @Query("enquiryId") enquiryId : Long,
                             @Query("status") status : String ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("api/enquiry/getAdvancedPaymentReceipt/{enquiryId}")
    fun getAdvancePaymentReceipt(@Header("Authorization") token:String,
                                 @Query("enquiryId") enquiryId : Long) : Call<PaymentReceiptResponse>

    @Headers("Accept: application/json")
    @GET("api/enquiry/getFinalPaymentReceipt/{enquiryId}")
    fun getFinalPaymentReceipt(@Header("Authorization") token:String,
                               @Query("enquiryId") enquiryId : Long) : Call<PaymentReceiptResponse>

    @Headers("Accept: application/json")
    @GET("api/enquiry/getPaymentDetailsForFinalPayment")
    fun getFinalPaymentDetails(@Header("Authorization") token:String,
                               @Query("enquiryId") enquiryId : Long) : Call<FinalPayDetailsResponse>

    @GET("api/transaction/getOngoingTransaction/{searchString}/{paymentType}")
    fun getAllOpenTransactions(@Header("Authorization") token:String,
//                               @Query("searchString") searchString : String,
                               @Query("paymentType") paymentType : Long) : Call<TransactionResponse>

    @GET("api/transaction/getCompletedTransaction/{searchString}/{paymentType}")
    fun getAllCompletedTransactions(@Header("Authorization") token:String,
//                                    @Query("searchString") searchString : String,
                                    @Query("paymentType") paymentType : Long) : Call<TransactionResponse>

    @GET("api/transaction/getTransactionStatus")
    fun getTransactionStatus(@Header("Authorization") token:String) : Call<TransactionStatusData>

    @GET("api/marketingTeam/getTransactions/{enquiryId}")
    fun getSingleTransaction(@Header("Authorization") token:String,
                             @Path("enquiryId") enquiryId : Int) : Call<SingleTransactionResponse>

//    fun registerUserPhoto(@Header("Content-Type") headerValue:String,
//                          @Query("registerRequest") registerRequest : String,
//                          @Body brandLogo : MultipartBody
//    ): Call<RegisterResponse>
}