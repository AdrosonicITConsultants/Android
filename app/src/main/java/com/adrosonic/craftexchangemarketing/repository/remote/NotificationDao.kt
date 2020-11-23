package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SearchProdData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface NotificationDao {

    @Headers("Accept: application/json")
    @GET("api/notification/getAllNotifications")
    fun getAllNotifications(@Header("Authorization") token: String): Call<NotificationResponse>

    @Headers("Accept: application/json")
    @POST("api/notification/markAsRead/{notificationId}")
    fun markSingleNotificationAsRead(@Header("Authorization") token: String,@Path("notificationId")notificationId:Long): Call<NotificationReadResponse>

    @Headers("Accept: application/json")
    @POST("api/notification/markAllAsRead")
    fun markAllNotificationsAsRead(@Header("Authorization") token:String) : Call<NotificationReadResponse>

}