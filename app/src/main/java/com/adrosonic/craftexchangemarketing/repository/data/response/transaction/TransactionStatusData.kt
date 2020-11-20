package com.adrosonic.craftexchangemarketing.repository.data.response.transaction

data class TransactionStatusData (
    val data: List<TranStatData>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class TranStatData (
    val id: Long,
    val transactionId: Long,
    val type: String,
    val viewType: String,
    val buyerText: String,
    val artisanText: String,
    val transactionStage: String,
    val buyerAction: Long,
    val artisanAction: Long
)
