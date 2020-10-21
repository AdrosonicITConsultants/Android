package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.changeRequest.RaiseCrInput
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchange.repository.data.response.changeReequest.ChatListResponse
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrOptionsResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.orders.OrderResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ChangeRequestDao {

    @Headers("Accept: application/json")
    @POST("/enquiry/toggleChangeRequestFromArtisan")
    fun toggleChangeRequestFromArtisan(@Header("Authorization") token:String,
                                       @Query("enquiryId") enquiryId : Int,
                                       @Query("status") status : Int) : Call<DeleteOwnProductRespons>

    @Headers("Accept: application/json")
    @GET("/enquiry/getChangeRequestItemTable")
    fun getChangeRequestItemTable(@Header("Authorization") token:String) : Call<CrOptionsResponse>


    @Headers("Accept: application/json")
    @GET("/enquiry/getChangeRequestForArtisan")
    fun getChangeRequestDetails(@Header("Authorization") token:String, @Query("enquiryId") enquiryId : Int) : Call<CrDetailsResponse>

    @Headers("Accept: application/json")
    @POST("/enquiry/changeRequest")
    fun raiseChangeRequest(@Header("Authorization") token:String,
                                       @Query("enquiryId") enquiryId : Int,
                                       @Body changeRequestParameters : RaiseCrInput
    ) : Call<NotificationReadResponse>//data field should be Change Request Updated

    @Headers("Accept: application/json")
    @POST("/enquiry/changeRequestStatusUpdate")
    fun changeRequestStatusUpdate(@Header("Authorization") token:String,
                                  @Body parameters : RaiseCrInput,
                                  @Query("status") status : Int
    ) : Call<NotificationReadResponse>//data field should be Change Request Updated


    @Headers("Accept: application/json")
    @GET("/enquiry/getEnquiryMessageChatList")
    fun getEnquiryMessageChatList(@Header("Authorization") token:String,
                                   @Query("searchedString") searchedString:String?) : Call<ChatListResponse>

}