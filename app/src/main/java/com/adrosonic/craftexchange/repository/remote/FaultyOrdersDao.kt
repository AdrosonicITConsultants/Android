package com.adrosonic.craftexchange.repository.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Query

interface FaultyOrdersDao {

    @Headers("Accept: application/json")
    @PUT("enquiry/updateFaultyOrderStatus")
    fun updateFaultyOrderStatus(@Header("Authorization") token: String,
                                @Query("enquiryId") enquiryId : Long,
                                @Query("isOrderReturned") isOrderReturned : Long,
                                @Query("isRefundReceived") isRefundReceived : Long ) : Call<ResponseBody>
}