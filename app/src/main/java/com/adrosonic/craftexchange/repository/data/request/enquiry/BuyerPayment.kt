package com.adrosonic.craftexchange.repository.data.request.enquiry

import org.jetbrains.annotations.Nullable


data class BuyerPayment (
    var enquiryId: Long,
    var invoiceId: Long?= null,
    var paidAmount: Long,
    var percentage: Long ?= null,
    var pid: Long ,
    var totalAmount: Long,
    var type: Long
)
