package com.adrosonic.craftexchange.repository.data.request.enquiry

data class BuyerPayment (
    var enquiryId: Long,
    var paidAmount: Long,
    var percentage: Long,
    var pid: Long,
    var totalAmount: Long,
    var type: Long
)
