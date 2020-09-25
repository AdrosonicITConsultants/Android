package com.adrosonic.craftexchange.repository.data.response.enquiry.payment


data class PaymentReceiptResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
    val id: Long,
    val label: String,
    val paymentId: Long,
    val isLatest: Long,
    val createdOn: String,
    val modifiedOn: String
)
