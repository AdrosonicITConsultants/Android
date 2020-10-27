package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.orders.OrderResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface OrderDao {

//    @Headers("Accept: application/json")
//    @GET("enquiry/getAllEnquiryStages")
//    fun getAllEnquiryStagesData(@Header("Authorization") token: String): Call<EnquiryStageData>
//
//    @Headers("Accept: application/json")
//    @GET("enquiry/getEnquiryStagesForAvailableProduct")
//    fun getAvailableProdEnquiryStagesData(@Header("Authorization") token: String): Call<EnquiryAvaProdStageData>


    @Headers("Accept: application/json")
    @GET("/order/getOpenOrders")
    fun getAllOpenOrders(@Header("Authorization") token:String) : Call<OrderResponse>

    @Headers("Accept: application/json")
    @GET("/order/getOrder/{enquiryId}")
    fun getSingleOngoingOrder(@Header("Authorization") token:String,
                              @Query("enquiryId") enquiryId : Int) : Call<OrderResponse>


    @Headers("Accept: application/json")
    @GET("/order/getClosedOrders")
    fun getAllClosedOrders(@Header("Authorization") token:String) : Call<OrderResponse>

    @Headers("Accept: application/json")
    @GET("/order/getClosedOrder/{enquiryId}")
    fun getSingleClosedOrder(@Header("Authorization") token:String,
                                @Query("enquiryId") enquiryId : Int) : Call<OrderResponse>


}