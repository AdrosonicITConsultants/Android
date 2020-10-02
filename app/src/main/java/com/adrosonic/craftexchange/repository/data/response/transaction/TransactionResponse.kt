package com.adrosonic.craftexchange.repository.data.response.transaction


data class TransactionResponse (
    val data: List<TransData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class TransData (
    val totalAmount: Long,
    val paidAmount: Long,
    val percentage: Long,
    val transactionOngoing: TransactionOngoing,
    val transactionCompleted: TransactionCompleted,
    val enquiryCode: String,
    val orderCode: String,
    val eta: String
)

data class TransactionOngoing (
    val id: Long,
    val enquiryId: Long,
    val piId: Long,
    val piHistoryId: Long,
    val paymentId: Long,
    val receiptId: Long,
    val taxInvoiceId: Long,
    val challanId: Long,
    val percentage: Long,
    val paidAmount: Long,
    val totalAmount: Long,
    val accomplishedStatus: Long,
    val upcomingStatus: Long,
    val isActionCompleted: Long,
    val isActive: Long,
    val transactionOn: String,
    val completedOn: String,
    val modifiedOn: String
)

data class TransactionCompleted (
    val id: Long,
    val enquiryId: Long,
    val piId: Long,
    val piHistoryId: Long,
    val paymentId: Long,
    val receiptId: Long,
    val taxInvoiceId: Long,
    val challanId: Long,
    val percentage: Long,
    val paidAmount: Long,
    val totalAmount: Long,
    val accomplishedStatus: Long,
    val upcomingStatus: Long,
    val isActionCompleted: Long,
    val isActive: Long,
    val transactionOn: String,
    val completedOn: String,
    val modifiedOn: String
)