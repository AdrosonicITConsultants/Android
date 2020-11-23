package com.adrosonic.craftexchangemarketing.repository.data.response.qc

data class QCQuestionData (
    val data: List<List<QuestionListData>>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class QuestionListData (
    val id: Long,
    val stageId: Long,
    val questionNo: Long,
    val question: String,
    val answerType: String,
    val optionValue: String? = null,
    val isDelete: Long
)