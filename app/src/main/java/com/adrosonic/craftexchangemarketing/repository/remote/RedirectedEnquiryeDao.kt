package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.EnquiryDataRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.FilteredArtisanResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnqiuiryListResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryOrderCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.ArtisanLT8Response
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.CustomEnquiriesResponse
import retrofit2.Call
import retrofit2.http.*

interface RedirectedEnquiryeDao {

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/getAdminIncomingEnquiries")
    fun getAdminCustomIncomingEnquiries(@Header("Authorization") token: String,
            @Query("pageNo")pageNo:Int,
            @Query("sortBy")sortBy:String,
            @Query("sortType")sortType:String
    ): Call<CustomEnquiriesResponse>

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/getAdminOtherIncomingEnquiries")
    fun getAdminOtherIncomingEnquiries(@Header("Authorization") token: String,
                                        @Query("pageNo")pageNo:Int,
                                        @Query("sortBy")sortBy:String,
                                        @Query("sortType")sortType:String
    ): Call<CustomEnquiriesResponse>

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/getAdminFaultyIncomingEnquiries")
    fun getAdminFaultyIncomingEnquiries(@Header("Authorization") token: String,
                                       @Query("pageNo")pageNo:Int,
                                       @Query("sortBy")sortBy:String,
                                       @Query("sortType")sortType:String
    ): Call<CustomEnquiriesResponse>

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/getArtisansLessThan8Rating")
    fun getArtisansLessThan8Rating(
        @Header("Authorization") token: String,
        @Query("clusterId") clusterId: Int,
        @Query("searchString") searchString: String,
        @Query("enquiryId") enquiryId: Int
    ): Call<ArtisanLT8Response>

    @Headers("Accept: application/json")
    @POST("api/marketingTeam/sendCustomEnquiry/{userIds}/{enquiryId}")
    fun sendCustomEnquiry(
        @Header("Authorization") token: String,
        @Path("userIds") userIds: String,
        @Path("enquiryId") enquiryId: Int
    ): Call<NotificationReadResponse>
 }