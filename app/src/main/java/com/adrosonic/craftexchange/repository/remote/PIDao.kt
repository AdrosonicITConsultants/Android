package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.moq.GetMoqsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendSelectedMoqResponse
import com.adrosonic.craftexchange.repository.data.response.pi.SendPiResponse
import retrofit2.Call
import retrofit2.http.*

interface PIDao {
    @Headers("Accept: application/json")
    @POST("/enquiry/sendPi/{enquiryId}")
    fun sendPI(@Header("Authorization") token:String,
                        @Path("enquiryId") enquiryId : Int,
                        @Body pi: SendPiRequest
    ) : Call<SendPiResponse>

//    @Headers("Accept: application/json")
//    @GET("/enquiry/getMoqDeliveryTimes")
//    fun getMoqDeliveryTimes(@Header("Authorization") token:String) : Call<MoqDeliveryTimesResponse>
//
//    @Headers("Accept: application/json")
//    @GET("/enquiry/getMoq/{enquiryId}")
//    fun getMoq(@Header("Authorization") token:String, @Path("enquiryId") enquiryId : Int) : Call<SendMoqResponse>
//
//    @Headers("Accept: application/json")
//    @GET("/enquiry/getMoqs/{enquiryId}")
//    fun getMoqs(@Header("Authorization") token:String, @Path("enquiryId") enquiryId : Int) : Call<GetMoqsResponse>
//
//    @Headers("Accept: application/json")
//    @POST("/enquiry/MoqSelected/{enquiryId}/{moqId}/{artisanId}")
//    fun moqSelected(@Header("Authorization") token:String,
//                    @Path("enquiryId") enquiryId : Int,
//                    @Path("moqId") moqId : Int,
//                    @Path("artisanId") artisanId : Int) : Call<SendSelectedMoqResponse>

}