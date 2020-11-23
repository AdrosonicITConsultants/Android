package com.adrosonic.craftexchangemarketing.repository.data.response.orders


data class OrderProgressResponse (
    val data: OPData,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class OPData(
    val orderProgress: OrderProgress,
    val brand: String,
    val totalAmount: Long,
    val productType: String
)

data class OrderProgress (
    val id: Long,
    val isFaulty: Long,
    val enquiryId: Long,
    val isResolved: Long,
    val isAutoClosed: Long,
    val buyerReviewComment: String,
    val artisanReviewComment: String,
    val artisanReviewId: String,
    val buyerReviewId: String,
    val orderDispatchDate: String,
    val orderReceiveDate: String,
    val orderCompleteDate: String,
    val orderDeliveryDate: String,
    val faultyMarkedOn: String,
    val isRefundReceived: Long,
    val isProductReturned: Long,
    val createdOn: String,
    val modifiedOn: String,
    val eta: String,
    val isPartialRefundReceived: Long,
    val partialRefundInitializedOn: Long,
    val partialRefundReceivedOn: Long
)
