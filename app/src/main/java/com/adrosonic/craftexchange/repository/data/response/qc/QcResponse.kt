package com.adrosonic.craftexchange.repository.data.response.qc

data class QcResponse (
    val data: QCData,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class QCData (
    val artisanQcResponses: List<List<ArtBuyQcResponse>>,
    val category: String,
    val artisanCompanyName: String,
    val buyerCompanyName: String,
    val stageId: Long,
    val isSend: Long
)

data class ArtBuyQcResponse (
    val id: Long,
    val enquiryId: Long,
    val stageId: Long,
    val questionId: Long,
    val artisanId: Long,
    val answer: String,
    val isSend: Long,
    val createdOn: String,
    val modifiedOn: String
)

