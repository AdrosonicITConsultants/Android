package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import retrofit2.Call
import retrofit2.http.*

interface EnquiryDao {

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
                        @Query("productId") productId : Long,
                        @Query("isCustom") isCustom : Boolean) : Call<IfExistEnquiryResponse>
}