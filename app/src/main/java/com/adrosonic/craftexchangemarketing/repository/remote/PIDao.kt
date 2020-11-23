package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchangemarketing.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.GetMoqsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.SendSelectedMoqResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.pi.SendPiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
interface PIDao {

    @Headers("Accept: application/json")
    @POST("api/enquiry/revisedPI")
    fun revisedPI(@Header("Authorization") token:String,
                  @Query("enquiryId") enquiryId : Int,
                  @Body pi: SendPiRequest
    ) : Call<NotificationReadResponse>


    @Headers("Accept: application/json")
    @POST("api/enquiry/sendPi/{enquiryId}")
    fun sendPI(@Header("Authorization") token:String,
               @Path("enquiryId") enquiryId : Int,
               @Body pi: SendPiRequest
    ) : Call<SendPiResponse>

    @Headers("Accept: application/json")
    @POST("api/enquiry/savePi/{enquiryId}")
    fun savePI(@Header("Authorization") token:String,
               @Path("enquiryId") enquiryId : Int,
               @Body pi: SendPiRequest
    ) : Call<SendPiResponse>

    @Headers("Accept: application/pdf")
    @GET("api/enquiry/getPreviewPiPDF")
    fun getPreviewPiPDF(@Header("Authorization") token:String, @Query("enquiryId") enquiryId : Int, @Query("isOld") isOld : String) : Call<ResponseBody>

    @Headers("Accept: text/html")
    @GET("api/enquiry/getPreviewPiHTML")
    fun getPreviewPiHTML(@Header("Authorization") token:String, @Query("enquiryId") enquiryId : Int, @Query("isOld") isOld : String) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("api/enquiry/getPi/{enquiryId}")
    fun getSinglePi(@Header("Authorization") token:String, @Path("enquiryId") enquiryId : Long) : Call<SendPiResponse>

    @Headers("Accept: application/json")
    @GET("api/enquiry/getOldPIData")
    fun getOldPiData(@Header("Authorization") token:String, @Query("enquiryId") enquiryId : Int) : Call<ResponseBody>


}