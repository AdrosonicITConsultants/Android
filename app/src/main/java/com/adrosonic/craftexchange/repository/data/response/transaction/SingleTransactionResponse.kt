package com.adrosonic.craftexchange.repository.data.response.transaction

data class SingleTransactionResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val completedTransactionResponses: List<CompletedTransactionResponse>,
    val ongoingTransactionResponses: List<CompletedTransactionResponse?>
)

data class CompletedTransactionResponse (
    val totalAmount: Long,
    val paidAmount: Long? = null,
    val percentage: Long? = null,
    val transactionCompleted: TransactionCompleted,
    val enquiryCode: String? = null,
    val orderCode: String,
    val eta: String? = null
)

