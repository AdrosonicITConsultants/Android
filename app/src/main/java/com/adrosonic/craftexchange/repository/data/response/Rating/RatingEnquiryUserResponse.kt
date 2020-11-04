package com.adrosonic.craftexchange.repository.data.response.Rating

data class RatingEnquiryUserResponse (
    val data : Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)
data class Data(
//    val questions : String,
    val artisanRating : ArrayList<QuestionRating>,
    val buyerRating : ArrayList<QuestionRating>,
    val isBuyerRatingDone : Int,
    val isArtisanRatingDone : Int
)

data class QuestionRating(
    val id : Long,
    val enquiryId : Long,
    val questionId : Int,
    val response :Float,
    val responseComment : String,
    val givenBy : Long,
    val givenTo : Long,
    val createdOn : String,
    val modifiedOn : String
)