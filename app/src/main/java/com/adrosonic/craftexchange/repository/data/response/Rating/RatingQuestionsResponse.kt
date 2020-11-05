package com.adrosonic.craftexchange.repository.data.response.Rating

data class RatingQuestionsResponse (
    val data : Data1,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data1(
//    val questions : String,
    val artisanQuestions : QuestionSet,
    val buyerQuestions : QuestionSet

)
data class QuestionSet(
    val ratingQuestions : ArrayList<Questions>,
    val commentQuestions : ArrayList<Questions>
)

data class Questions(
    val id : Long,
    val question : String,
    val questionNo : Int,
    val type :Int,
    val isDelete : Int,
    val createdOn : String,
    val modifiedOn : String,
    val answerType : Int
)