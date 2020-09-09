package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import retrofit2.Call
import retrofit2.http.*

interface MoqDao {
    @Headers("Accept: application/json")
    @POST("/enquiry/sendMoq/{enquiryId}")
    fun sendMoq(@Header("Authorization") token:String,
                        @Path("enquiryId") enquiryId : Int,
                        @Body moq: SendMoqRequest
    ) : Call<SendMoqResponse>

    @Headers("Accept: application/json")
    @GET("/enquiry/getMoqDeliveryTimes")
    fun getMoqDeliveryTimes(@Header("Authorization") token:String) : Call<MoqDeliveryTimesResponse>

    @Headers("Accept: application/json")
    @GET("/enquiry/getMoq/{enquiryId}")
    fun getMoq(@Header("Authorization") token:String, @Path("enquiryId") enquiryId : Int) : Call<SendMoqResponse>
}