package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.qc.SaveOrSendQcRequest
import com.adrosonic.craftexchange.repository.data.request.qc.sSaveOrSendQcRequest
import com.adrosonic.craftexchange.repository.data.response.enquiry.InnerStageData
import com.adrosonic.craftexchange.repository.data.response.qc.BuyerQcResponse
import com.adrosonic.craftexchange.repository.data.response.qc.QCQuestionData
import com.adrosonic.craftexchange.repository.data.response.qc.QcResponse
import com.adrosonic.craftexchange.repository.data.response.qc.SaveSendQcResponse
import retrofit2.Call
import retrofit2.http.*

interface QCDao {
    @Headers("Accept: application/json")
    @GET("qc/getStages")
    fun getQCStages(@Header("Authorization") token: String) : Call<InnerStageData>

    @Headers("Accept: application/json")
    @GET("qc/getAllQuestions")
    fun getQCQuestionData(@Header("Authorization") token: String) : Call<QCQuestionData>

    @Headers("Accept: application/json")
    @POST("qc/sendOrSaveQcForm")
    fun sendOrSaveQcForm(@Header("Authorization") token:String,
                         @Body qcRequests: SaveOrSendQcRequest) : Call<SaveSendQcResponse>

    @Headers("Accept: application/json")
    @GET("qc/getArtisanQcResponse")
    fun getArtisanQcResponses(@Header("Authorization") token:String,
                         @Query("enquiryId")enquiryId:Long) : Call<QcResponse>

    @Headers("Accept: application/json")
    @GET("qc/getArtisanQcResponse")
    fun getBuyerQcResponses(@Header("Authorization") token:String,
                            @Query("enquiryId")enquiryId:Long) : Call<QcResponse>

//    @Headers("Accept: application/json")
//    @GET("qc/getBuyerQcResponse")
//    fun getBuyerQcResponses(@Header("Authorization") token:String,
//                              @Query("enquiryId")enquiryId:Long) : Call<BuyerQcResponse>
}