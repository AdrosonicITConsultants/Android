package com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase


data class EnquiryOrderCountResponse(
    var data: ArrayList<Data>,
    var errorCode: Int,
    var errorMessage: Any,
    var valid: Boolean
)
data class Data(
    var totalOrders: Long,
    var totalEnquiries: Long,
    var ongoingEnquiries: Long,
    var  enquiriesConverted: Long,
    var incompleteAndClosedEnquiries: Long,
    var orderCompletedSuccessfully: Long,
    var incompleteAndClosedOrders: Long,
    var faultyInResolution: Long,
    var escaltions: Long,
    var paymentsTransactions: Long,
    var deliveryFaultyOrder: Long,
    var chatEscalation: Long,
    var awaitingMoqResponse: Long,
    var awaitingMoq: Long,
    var updates: Long,
    var ongoingOrders: Long
)