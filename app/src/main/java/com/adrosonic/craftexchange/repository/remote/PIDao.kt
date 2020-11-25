package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.moq.GetMoqsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendSelectedMoqResponse
import com.adrosonic.craftexchange.repository.data.response.pi.SendPiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface PIDao {

    @Headers("Accept: application/json")
    @POST("enquiry/revisedPI")
    fun revisedPI(@Header("Authorization") token:String,
                  @Query("enquiryId") enquiryId : Int,
                  @Body pi: SendPiRequest
    ) : Call<NotificationReadResponse>


    @Headers("Accept: application/json")
    @POST("enquiry/sendPi/{enquiryId}")
    fun sendPI(@Header("Authorization") token:String,
                        @Path("enquiryId") enquiryId : Int,
                        @Body pi: SendPiRequest
    ) : Call<SendPiResponse>

    @Headers("Accept: application/json")
    @POST("enquiry/savePi/{enquiryId}")
    fun savePI(@Header("Authorization") token:String,
               @Path("enquiryId") enquiryId : Int,
               @Body pi: SendPiRequest
    ) : Call<SendPiResponse>

    @Headers("Accept: application/pdf")
    @GET("enquiry/getPreviewPiPDF")
    fun getPreviewPiPDF(@Header("Authorization") token:String, @Query("enquiryId") enquiryId : Int, @Query("isOld") isOld : String) : Call<ResponseBody>

    @Headers("Accept: text/html")
    @GET("enquiry/getPreviewPiHTML")
    fun getPreviewPiHTML(@Header("Authorization") token:String, @Query("enquiryId") enquiryId : Int, @Query("isOld") isOld : String) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("enquiry/getPi/{enquiryId}")
    fun getSinglePi(@Header("Authorization") token:String, @Path("enquiryId") enquiryId : Long) : Call<SendPiResponse>

    @Headers("Accept: application/json")
    @GET("enquiry/getOldPIData")
    fun getOldPiData(@Header("Authorization") token:String, @Query("enquiryId") enquiryId : Int) : Call<ResponseBody>


}