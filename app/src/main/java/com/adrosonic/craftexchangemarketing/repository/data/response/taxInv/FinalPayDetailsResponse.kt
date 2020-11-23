package com.adrosonic.craftexchange.repository.data.response.taxInv

data class FinalPayDetailsResponse (
    val data: FinPayData,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class FinPayData (
    val payableAmount: Long,
    val invoiceId: Long,
    val totalAmount: Long,
    val pid: Long,
    val finalPaymentDone: Boolean
)
