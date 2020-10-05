package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.enquiry.payment.PaymentReceiptResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.SingleTransactionResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.TransactionResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.TransactionStatusData
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

    @GET("transaction/getOngoingTransaction/{searchString}/{paymentType}")
    fun getAllOpenTransactions(@Header("Authorization") token:String,
//                               @Query("searchString") searchString : String,
                               @Query("paymentType") paymentType : Long) : Call<TransactionResponse>

    @GET("transaction/getCompletedTransaction/{searchString}/{paymentType}")
    fun getAllCompletedTransactions(@Header("Authorization") token:String,
//                                    @Query("searchString") searchString : String,
                                    @Query("paymentType") paymentType : Long) : Call<TransactionResponse>

    @GET("transaction/getTransactionStatus")
    fun getTransactionStatus(@Header("Authorization") token:String) : Call<TransactionStatusData>

    @GET("/transaction/getTransactions/{enquiryId}")
    fun getSingleTransaction(@Header("Authorization") token:String,
                               @Path("enquiryId") enquiryId : Int) : Call<SingleTransactionResponse>

}
