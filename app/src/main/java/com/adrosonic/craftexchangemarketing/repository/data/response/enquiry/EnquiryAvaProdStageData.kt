package com.adrosonic.craftexchangemarketing.repository.data.response.enquiry

data class EnquiryAvaProdStageData (
    val data: List<APStageData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class APStageData (
    val num: Long,
    val orderStages: OrderStages
)

data class OrderStages (
    val id: Long,
    val desc: String
)
