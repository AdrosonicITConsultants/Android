package com.adrosonic.craftexchangemarketing.repository.data.response.transaction

data class SingleTransactionResponse(
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data(
    val completedTransactionResponses: List<CompletedTransactionResponse?>,
    val ongoingTransactionResponses: List<OngoingTransactionResponse?>
)

data class CompletedTransactionResponse(
    val totalAmount: Long? = null,
    val paidAmount: Long? = null,
    val percentage: Long? = null,
    val transactionCompleted: TransactionCompleted,
    val enquiryCode: String? = null,
    val orderCode: String,
    val eta: String? = null
)
data class OngoingTransactionResponse (
    val totalAmount: Long? = null,
    val paidAmount: Long? = null,
    val percentage: Long? = null,
    val transactionOngoing: TransactionOngoing,
    val enquiryCode: String? = null,
    val orderCode: String,
    val eta: String? = null
)
//data class TransactionCompleted1(
//    val id: Long,
//    val enquiryID: Long,
//    val piID: Long? = null,
//    val piHistoryID: Any? = null,
//    val paymentID: Long? = null,
//    val receiptID: Any? = null,
//    val taxInvoiceID: Long? = null,
//    val challanID: Long? = null,
//    val percentage: Any? = null,
//    val paidAmount: Long? = null,
//    val totalAmount: Long? = null,
//    val accomplishedStatus: Long,
//    val upcomingStatus: Long,
//    val isActionCompleted: Long,
//    val isActive: Long,
//    val transactionOn: String,
//    val completedOn: String,
//    val modifiedOn: String
//)
//
//data class TransactionOngoing1 (
//    val id: Long,
//    val enquiryID: Long,
//    val piID: Long? = null,
//    val piHistoryID: Any? = null,
//    val paymentID: Long? = null,
//    val receiptID: Any? = null,
//    val taxInvoiceID: Long? = null,
//    val challanID: Long? = null,
//    val percentage: Any? = null,
//    val paidAmount: Long? = null,
//    val totalAmount: Long? = null,
//    val accomplishedStatus: Long,
//    val upcomingStatus: Long,
//    val isActionCompleted: Long,
//    val isActive: Long,
//    val transactionOn: String,
//    val completedOn: String,
//    val modifiedOn: String
//)
