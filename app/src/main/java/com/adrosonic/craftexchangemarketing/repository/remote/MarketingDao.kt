package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.marketing.ArtisanDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.GetMoqsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.SendSelectedMoqResponse
import retrofit2.Call
import retrofit2.http.*

interface MarketingDao {

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/userProfile/{userId}")
    fun getArtisanDao(@Header("Authorization") token:String, @Path("userId")userId:Long) : Call<ArtisanDetailsResponse>
}