package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryOrderCountResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface EnquiryOrderDao {

    @Headers("Accept: application/json")
    @GET("/marketingTeam/getAdminEnquiryAndOrder")
    fun getEnquiryOrderHomeCounts(@Header("Authorization") token: String): Call<EnquiryOrderCountResponse>
}