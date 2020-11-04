package com.adrosonic.craftexchange.repository.data.response.faultyOrders

data class FaultReviewRefResponse (
    val data: List<FaultRefData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class FaultRefData (
    var id: Long,
    var comment: String,
    var subComment: String,
    var isSelected : Boolean
)