package com.adrosonic.craftexchange.repository.data.response.orders.advPayment

data class RviseAdvPaymentStatusResponse (
    val data: RevisedStatusResponse,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class RevisedStatusResponse (
    val totalAmount: Long,
    val paidAmount: Long,
    val pendingAmount: Long,
    val percentage: Long,
    val piId: Long
)
