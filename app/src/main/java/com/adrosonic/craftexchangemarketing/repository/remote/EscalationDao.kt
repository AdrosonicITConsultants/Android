package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.EscalationCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.EscalationResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.ResolvedEscalationResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.UserDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

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
}