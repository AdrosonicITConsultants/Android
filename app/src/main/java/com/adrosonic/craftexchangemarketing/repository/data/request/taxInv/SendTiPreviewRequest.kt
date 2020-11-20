package com.adrosonic.craftexchangemarketing.repository.data.request.taxInv

import java.io.Serializable

open class SendTiPreviewRequest : Serializable{
    var advancePaidAmt: Long ?= 0
    var cgst: Long?= 0
    var sgst: Long?= 0
    var deliveryCharges: Long?= 0
    var enquiryId: Long?= 0
    var finalTotalAmt: Long?= 0
    var ppu: Long?= 0
    var quantity: Long?= 0

    var isactive: Long?= 0
    var createdon: DateeFormat ?= null
    var date: DateeFormat ?= null
    var modifiedon: DateeFormat ?= null
    var piAmt: Long?= 0
    var invoiceNo: String?= ""
    var id: Long?= 0
    var hsn: Long?= 0
    var artisanId: Long?= 0
}



