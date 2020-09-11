package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.response.marketing.ArtisanDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.GetMoqsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendSelectedMoqResponse
import retrofit2.Call
import retrofit2.http.*

interface MarketingDao {

    @Headers("Accept: application/json")
    @GET("/marketingTeam/userProfile/{userId}")
    fun getArtisanDao(@Header("Authorization") token:String, @Path("userId")userId:Long) : Call<ArtisanDetailsResponse>


}