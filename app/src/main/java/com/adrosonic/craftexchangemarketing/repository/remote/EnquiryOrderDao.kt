package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.EnquiryDataRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnqiuiryListResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryOrderCountResponse
import retrofit2.Call
import retrofit2.http.*

interface EnquiryOrderDao {

    @Headers("Accept: application/json")
    @GET("/marketingTeam/getAdminEnquiryAndOrder")
    fun getEnquiryOrderHomeCounts(@Header("Authorization") token: String): Call<EnquiryOrderCountResponse>

    @Headers("Accept: application/json")
    @POST("/marketingTeam/getAdminEnquiries")
    fun getEnquiryList(@Header("Authorization") token: String,
                       @Body adminEnquiriesRequest : EnquiryDataRequest
                        ): Call<EnqiuiryListResponse>

    @Headers("Accept: application/json")
    @POST("/marketingTeam/getAdminIncompletedAndClosedEnquiries")
    fun getEnquiryCompletedList(@Header("Authorization") token: String,
                                @Body adminEnquiriesRequest : EnquiryDataRequest
    ): Call<EnqiuiryListResponse>

    @Headers("Accept: application/json")
    @POST("/marketingTeam/getAdminOrders")
    fun getOrderList(@Header("Authorization") token: String,
                                @Body adminEnquiriesRequest : EnquiryDataRequest
    ): Call<EnqiuiryListResponse>

    @Headers("Accept: application/json")
    @POST("/marketingTeam/getAdminIncompletedAndClosedOrders")
    fun getOrderIncompletedList(@Header("Authorization") token: String,
                                @Body adminEnquiriesRequest : EnquiryDataRequest
    ): Call<EnqiuiryListResponse>


}