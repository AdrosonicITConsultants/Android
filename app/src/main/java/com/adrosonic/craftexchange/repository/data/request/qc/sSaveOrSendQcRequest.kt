package com.adrosonic.craftexchange.repository.data.request.qc

import java.io.Serializable

//data class SaveOrSendQcRequest (
//    val enquiryId: Long,
//    val questionAnswers: List<QuestionAnswer>,
//    val saveOrSend: Long,
//    val stageId: Long
//)
//
//data class QuestionAnswer (
//    val answer: String,
//    val questionId: Long
//)

open class sSaveOrSendQcRequest : Serializable {
    var enquiryId: Long ?= 0
    var questionAnswers: List<sQuestionAnswer> ?= null
    var saveOrSend: Long ?= 0
    var stageId: Long ?= 0
}

open class sQuestionAnswer : Serializable {
    var answer: String ?= ""
    var questionId: Long ?= 0
}


data class SaveOrSendQcRequest (
    var enquiryId: Long ?= 0,
    var questionAnswers: ArrayList<QuestionAnswer> ?= null,
    var saveOrSend: Long ?= 0,
    var stageId: Long ?= 0

)


data class QuestionAnswer (
    var questionId: Long ?= 0,
    var answer: String ?= ""
)

