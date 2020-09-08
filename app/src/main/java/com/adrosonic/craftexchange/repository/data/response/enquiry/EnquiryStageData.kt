package com.adrosonic.craftexchange.repository.data.response.enquiry

data class EnquiryStageData (
    val data: List<StageData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class StageData (
    val id: Long,
//    val stageDesc: String
val desc : String
)
