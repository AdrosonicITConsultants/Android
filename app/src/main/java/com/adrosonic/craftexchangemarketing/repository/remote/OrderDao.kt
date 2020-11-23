package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryStageData
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.orders.OrderProgressResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.orders.OrderResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface OrderDao {

//    @Headers("Accept: application/json")
//    @GET("api/enquiry/getAllEnquiryStages")
//    fun getAllEnquiryStagesData(@Header("Authorization") token: String): Call<EnquiryStageData>
//
//    @Headers("Accept: application/json")
//    @GET("api/enquiry/getEnquiryStagesForAvailableProduct")
//    fun getAvailableProdEnquiryStagesData(@Header("Authorization") token: String): Call<EnquiryAvaProdStageData>

    @Headers("Accept: application/json")
    @GET("api/enquiry/getOrderProgress/{orderId}")
    fun getOrderProgress(@Header("Authorization") token:String,
                         @Path("orderId")orderId:Long) : Call<OrderProgressResponse>

    @Headers("Accept: application/json")
    @GET("api/order/getOpenOrders")
    fun getAllOpenOrders(@Header("Authorization") token:String) : Call<OrderResponse>

    @Headers("Accept: application/json")
    @GET("api/order/getOrder/{enquiryId}")
    fun getSingleOngoingOrder(@Header("Authorization") token:String,
                              @Query("enquiryId") enquiryId : Int) : Call<OrderResponse>


    @Headers("Accept: application/json")
    @GET("api/order/getClosedOrders")
    fun getAllClosedOrders(@Header("Authorization") token:String) : Call<OrderResponse>

    @Headers("Accept: application/json")
    @GET("api/order/getClosedOrder/{enquiryId}")
    fun getSingleClosedOrder(@Header("Authorization") token:String,
                                @Query("enquiryId") enquiryId : Int) : Call<OrderResponse>

    @Headers("Accept: application/json")
    @POST("api/enquiry/markOrderAsRecieved/{orderId}/{orderRecieveDate}/{isAutoCompleted}")
    fun markOrderAsReceived(@Header("Authorization") token:String,
                             @Path("orderId") orderId : Int,
                             @Path("orderRecieveDate") orderRecieveDate : String,
                             @Path("isAutoCompleted") isAutoCompleted : Int
    ) : Call<NotificationReadResponse>

    @Headers("Accept: application/json")
    @POST("api/enquiry/markEnquiryCompleted/{enquiryId}")
    fun markEnquiryCompleted(@Header("Authorization") token:String,
                             @Path("enquiryId") enquiryId : Long
    ) : Call<NotificationReadResponse>

    @Headers("Accept: application/json")
    @POST("api/order/recreateOrder")
    fun recreateOrder(@Header("Authorization") token:String,
                             @Query("orderId") orderId : Long
    ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("api/order/orderDispatchAfterRecreation")
    fun orderDispatchAfterRecreation(@Header("Authorization") token:String,
                      @Query("orderId") orderId : Long
    ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("api/order/initializePartialRefund")
    fun initializePartialRefund(@Header("Authorization") token:String,
                             @Query("orderId") orderId : Long
    ) : Call<NotificationReadResponse>
}