package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.chat.RaiseEscalationRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.chat.ChatData
import com.adrosonic.craftexchangemarketing.repository.data.response.chat.ChatListResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.chat.ChatLogListData
import com.adrosonic.craftexchangemarketing.repository.data.response.chat.escalations.EscalationCategoryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.chat.escalations.EscalationSummResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.InnerStageData
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ChatListDao {

    @Headers("Accept: application/json")
    @GET("/enquiry/getEnquiryMessageChatList")
    fun getInitiatedChatList(@Header("Authorization") token:String,
                             @Query("searchedString") searchedString:String?) : Call<ChatListResponse>

    @Headers("Accept: application/json")
    @GET("/enquiry/getNewEnquiryMessageChatList")
    fun getUninitiatedChatList(@Header("Authorization") token:String,
                               @Query("searchedString") searchedString:String?) : Call<ChatListResponse>

    @Headers("Accept: application/json")
    @POST("/enquiry/goToEnquiryChat/")
    fun initiateChat(@Header("Authorization") token:String,
                     @Query("enquiryId") enquiryId : Long) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("/marketingTeam/getChatMessages")
    fun openChatLog(@Header("Authorization") token:String,
                    @Query("enquiryId") enquiryId : Long)  : Call<ChatLogListData>

    @Headers("Accept: application/json")
    @POST("/enquiry/sendChatboxMessage/")
    fun sendChatboxMessage(@Header("Authorization") token:String,
                           @Query("messageJson") messageJson : String) : Call<NotificationReadResponse>

    @Headers("Accept: application/json")
    @POST("/enquiry/sendChatboxMessage/")
    fun sendChatboxMessageWithMedia(@Header("Authorization") token:String,
                           @Header("Content-Type") headerValue:String,
                           @Header("Content-Length") length: Long,
                           @Query("messageJson") messageJson : String,
                           @Body file: MultipartBody?) : Call<NotificationReadResponse>

    @Headers("Accept: application/json")
    @GET("ChatBoxMedia/{enquiryId}/{imagename}")
    fun getChatMedia(
        @Path("enquiryId") enquiryId: Long,
        @Path("imagename") imagename: String
    ):Call<ResponseBody>


    @Headers("Accept: application/json")
    @GET("User/{artisanId}/CompanyDetails/Logo/{imagename}")
    fun getBuyerLogo(
        @Path("artisanId") artisanId: Long,
        @Path("imagename") imagename: String
    ):Call<ResponseBody>


    @Headers("Accept: application/json")
    @GET("enquiry/getEscalations")
    fun getEscalationData(@Header("Authorization") token:String) : Call<EscalationCategoryResponse>

    @Headers("Accept: application/json")
    @GET("enquiry/getEscalationSummaryForEnquiry")
    fun getEscalationSummary(@Header("Authorization") token:String,
                             @Query("enquiryId")enquiryId:Long) : Call<EscalationSummResponse>

    @Headers("Accept: application/json")
    @POST("enquiry/resolveEscalation")
    fun resolveEscalation(@Header("Authorization") token:String,
                          @Query("escalationId")escalationId:Long) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("enquiry/raiseEscalaton")
    fun raiseEscalation(@Header("Authorization") token:String,
                          @Body escalation:RaiseEscalationRequest) : Call<ResponseBody>
}
