package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface EnquiryDao {

//    GET /enquiry/getAllEnquiryStages
    @Headers("Accept: application/json")
    @GET("enquiry/getAllEnquiryStages")
    fun getAllEnquiryStagesData(@Header("Authorization") token: String): Call<EnquiryStageData>

    @Headers("Accept: application/json")
    @GET("enquiry/getEnquiryStagesForAvailableProduct")
    fun getAvailableProdEnquiryStagesData(@Header("Authorization") token: String): Call<EnquiryAvaProdStageData>

    @Headers("Accept: application/json")
    @POST("enquiry/generateEnquiry/{productId}/{isCustom}/{deviceName}")
    fun generateEnquiry(@Header("Authorization") token:String,
                      @Path("productId") productId : Long,
                        @Path("isCustom") isCustom : Boolean,
                        @Path("deviceName") deviceName : String
    ) : Call<GenerateEnquiryResponse>

    @Headers("Accept: application/json")
    @GET("enquiry/ifEnquiryExists/{productId}/{isCustom}")
    fun ifEnquiryExists(@Header("Authorization") token:String,
                        @Path("productId") productId : Long,
                        @Path("isCustom") isCustom : Boolean) : Call<IfExistEnquiryResponse>

    @Headers("Accept: application/json")
    @GET("enquiry/getOpenEnquiries")
    fun getAllOngoingEnquiries(@Header("Authorization") token:String) : Call<EnquiryResponse>

    @Headers("Accept: application/json")
    @GET("enquiry/getEnquiry/{enquiryId}")
    fun getSingleOngoingEnquiry(@Header("Authorization") token:String,
                                @Path("enquiryId") enquiryId : Long) : Call<EnquiryResponse>

    @Headers("Accept: application/json")
    @POST("enquiry/markEnquiryCompleted/{enquiryId}")
    fun markEnquiryCompleted(@Header("Authorization") token:String,
                            @Path("enquiryId") enquiryId : Long
    ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("enquiry/getClosedEnquiries")
    fun getAllCompletedEnquiries(@Header("Authorization") token:String) : Call<EnquiryResponse>

    @Headers("Accept: application/json")
    @GET("enquiry/getClosedEnquiry/{enquiryId}")
    fun getSingleCompletedEnquiry(@Header("Authorization") token:String,
                                @Path("enquiryId") enquiryId : Long) : Call<EnquiryResponse>

}