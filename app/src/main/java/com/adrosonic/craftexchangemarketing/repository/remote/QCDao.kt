package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.qc.SaveOrSendQcRequest
import com.adrosonic.craftexchangemarketing.repository.data.request.qc.sSaveOrSendQcRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.InnerStageData
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.BuyerQcResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.QCQuestionData
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.QcResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.SaveSendQcResponse
import retrofit2.Call
import retrofit2.http.*

interface QCDao {
    @Headers("Accept: application/json")
    @GET("api/qc/getStages")
    fun getQCStages(@Header("Authorization") token: String) : Call<InnerStageData>

    @Headers("Accept: application/json")
    @GET("api/qc/getAllQuestions")
    fun getQCQuestionData(@Header("Authorization") token: String) : Call<QCQuestionData>

    @Headers("Accept: application/json")
    @POST("api/qc/sendOrSaveQcForm")
    fun sendOrSaveQcForm(@Header("Authorization") token:String,
                         @Body qcRequests: SaveOrSendQcRequest) : Call<SaveSendQcResponse>

    @Headers("Accept: application/json")
    @GET("api/qc/getArtisanQcResponse")
    fun getArtisanQcResponses(@Header("Authorization") token:String,
                         @Query("enquiryId")enquiryId:Long) : Call<QcResponse>

    @Headers("Accept: application/json")
    @GET("api/qc/getArtisanQcResponse")
    fun getBuyerQcResponses(@Header("Authorization") token:String,
                            @Query("enquiryId")enquiryId:Long) : Call<QcResponse>

//    @Headers("Accept: application/json")
//    @GET("api/qc/getBuyerQcResponse")
//    fun getBuyerQcResponses(@Header("Authorization") token:String,
//                              @Query("enquiryId")enquiryId:Long) : Call<BuyerQcResponse>
}