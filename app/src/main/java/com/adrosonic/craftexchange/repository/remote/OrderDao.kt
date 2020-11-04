package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.orders.OrderProgressResponse
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
    @GET("enquiry/getOrderProgress/{orderId}")
    fun getOrderProgress(@Header("Authorization") token:String,
                         @Path("orderId")orderId:Long) : Call<OrderProgressResponse>

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

    @Headers("Accept: application/json")
    @POST("/enquiry/markOrderAsRecieved/{orderId}/{orderRecieveDate}/{isAutoCompleted}")
    fun markOrderAsReceived(@Header("Authorization") token:String,
                             @Path("orderId") orderId : Int,
                             @Path("orderRecieveDate") orderRecieveDate : String,
                             @Path("isAutoCompleted") isAutoCompleted : Int
    ) : Call<NotificationReadResponse>

    @Headers("Accept: application/json")
    @POST("enquiry/markEnquiryCompleted/{enquiryId}")
    fun markEnquiryCompleted(@Header("Authorization") token:String,
                             @Path("enquiryId") enquiryId : Long
    ) : Call<NotificationReadResponse>

    @Headers("Accept: application/json")
    @POST("order/recreateOrder")
    fun recreateOrder(@Header("Authorization") token:String,
                             @Query("orderId") orderId : Long
    ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("order/orderDispatchAfterRecreation")
    fun orderDispatchAfterRecreation(@Header("Authorization") token:String,
                      @Query("orderId") orderId : Long
    ) : Call<ResponseBody>
}