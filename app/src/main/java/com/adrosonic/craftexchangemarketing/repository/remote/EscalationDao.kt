package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.escalation.GetNewArtisanEnqRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.*
import com.adrosonic.craftexchangemarketing.repository.data.response.escalationa.GetNewArtisanEnqResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.ArtisanLT8Response
import retrofit2.Call
import retrofit2.http.*

interface EscalationDao {

    @GET("api/marketingTeam/getAdminEscalationResponses")
    fun getEscUpdates(
        @Header("Authorization") token: String,
        @Query("category") category: String,
        @Query("pageNo") pageNo: Long,
        @Query("sortBy") sortBy: String,
        @Query("sortType") sortType:String,
        @Query("searchStr") searchStr:String?
    ): Call<EscalationResponse>

    @GET("api/marketingTeam/getAdminEscalationResponsesCount")
    fun getEscalationCount(
        @Header("Authorization") token: String,
        @Query("category") category: String,
        @Query("sortBy") sortBy: String,
        @Query("sortType") sortType:String,
        @Query("searchStr") searchStr:String?
    ): Call<EscalationCountResponse>

    @GET("api/marketingTeam/getUserDetails")
    fun getUserDetails(
        @Header("Authorization") token: String,
        @Query("enquiryId") enquiryId: Long
    ): Call<UserDataResponse>

    @POST("api/enquiry/resolveEscalation")
    fun markResolved(
        @Header("Authorization") token: String,
        @Query("escalationId") escalationId: Long
    ): Call<ResolvedEscalationResponse>

    @POST("api/marketingTeam/createNewEnquiry")
    fun createNewEnquiry(
        @Header("Authorization") token: String,
        @Query("enquiryId") enquiryId: Long,
        @Query("deviceName") deviceName: String
    ): Call<GenerateEnqResponse>

    @POST("api/marketingTeam/getArtisansForNewEnquiry")
    fun getArtisansForNewEnquiry(
        @Header("Authorization") token: String,
        @Body artisansForNewEnquiry: GetNewArtisanEnqRequest
    ): Call<GetNewArtisanEnqResponse>
}