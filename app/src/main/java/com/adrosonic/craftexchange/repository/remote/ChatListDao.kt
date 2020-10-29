package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.chat.ChatData
import com.adrosonic.craftexchange.repository.data.response.chat.ChatListResponse
import com.adrosonic.craftexchange.repository.data.response.chat.ChatLogListData
import com.adrosonic.craftexchange.repository.data.response.enquiry.InnerStageData
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
    @GET(" /enquiry/getAndReadChatMessageForEnquiry")
    fun openChatLog(@Header("Authorization") token:String,
                    @Query("enquiryId") enquiryId : Long)  : Call<ChatLogListData>
}