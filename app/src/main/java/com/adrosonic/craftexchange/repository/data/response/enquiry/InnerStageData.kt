package com.adrosonic.craftexchange.repository.data.response.enquiry


data class InnerStageData (
    val data: List<IStageData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class IStageData (
    val id: Long,
    val stage: String
)