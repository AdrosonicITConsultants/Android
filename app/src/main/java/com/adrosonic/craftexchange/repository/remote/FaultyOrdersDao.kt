package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.faultyOrders.FaultReviewRefResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FaultyOrdersDao {

    @Headers("Accept: application/json")
    @GET("enquiry/getAllRefBuyerReview")
    fun getAllRefBuyerReview(@Header("Authorization") token: String) : Call<FaultReviewRefResponse>

    @Headers("Accept: application/json")
    @GET("enquiry/getAllRefArtisanReview")
    fun getAllRefArtisanReview(@Header("Authorization") token: String) : Call<FaultReviewRefResponse>

    @Headers("Accept: application/json")
    @POST("enquiry/faultyOrderBuyer/{orderId}/{buyerComment}/{multiCheck}")
    fun postBuyerFaultReview(@Header("Authorization") token: String,
                             @Path("orderId")orderId:String,
                             @Path("buyerComment")buyerComment:String,
                             @Path("multiCheck")multiCheck:String) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("enquiry/faultyOrderArisan/{orderId}/{artisanReviewComment}/{multiCheck}")
    fun postArtisanFaultReview(@Header("Authorization") token: String,
                             @Path("orderId")orderId:String,
                             @Path("artisanReviewComment")artisanReviewComment:String,
                             @Path("multiCheck")multiCheck:String) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @PUT("enquiry/updateFaultyOrderStatus")
    fun updateFaultyOrderStatus(@Header("Authorization") token: String,
                                @Query("enquiryId") enquiryId : Long,
                                @Query("isOrderReturned") isOrderReturned : Long,
                                @Query("isRefundReceived") isRefundReceived : Long ) : Call<ResponseBody>
}