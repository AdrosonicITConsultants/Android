package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.editProfileModel.PaymentAccountDetails
import com.adrosonic.craftexchange.repository.data.request.rating.RatingRequest
import com.adrosonic.craftexchange.repository.data.response.Rating.RatingEnquiryUserResponse
import com.adrosonic.craftexchange.repository.data.response.Rating.RatingQuestionsResponse
import com.adrosonic.craftexchange.repository.data.response.Rating.SendRatingResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.editProfile.EditBankDetailsResponse
import retrofit2.Call
import retrofit2.http.*

interface RatingDao {

    @Headers("Accept: application/json")
    @GET("/user/getRatingsForUser")
    fun getRatingEnquiryUser(@Header("Authorization") token:String,
                             @Query("enquiryId") enquiryId : Long,
                             @Query("userId") userId : Long
    ) : Call<RatingEnquiryUserResponse>

    @Headers("Accept: application/json")
    @GET("/user/getRatingQuestions")
    fun getRatingQuestions(@Header("Authorization") token:String
    ) : Call<RatingQuestionsResponse>

    @Headers("Accept: application/json")
    @POST("/user/submitRatingToUser")
    fun sendRatings(@Header("Authorization") token:String,
                               @Body rating : ArrayList<RatingRequest>) : Call<SendRatingResponse>

}