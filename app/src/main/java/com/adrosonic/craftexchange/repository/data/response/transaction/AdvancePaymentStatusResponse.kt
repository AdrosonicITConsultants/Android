package com.adrosonic.craftexchange.repository.data.response.transaction

data class AdvancePaymentStatusResponse (
    val data: AdvancePaymentStatus,
    val valid: Boolean,
    val errorMessage: String? = null,
    val errorCode: Long
)

data class AdvancePaymentStatus (
    val id: Long,
    val type: Long,
    val enquiryID: Long,
    val pid: Long,
    val invoiceID: Any? = null,
    val percentage: Long,
    val paidAmount: Long,
    val totalAmount: Long,
    val receivedOn: String,
    val rejectedOn: Any? = null,
    val status: Long,
    val createdOn: String,
    val modifiedOn: String
)