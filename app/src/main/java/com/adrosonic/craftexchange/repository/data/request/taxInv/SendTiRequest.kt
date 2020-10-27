package com.adrosonic.craftexchange.repository.data.request.taxInv


data class SendTiRequest(
    var advancePaidAmt: String?= "",
    var cgst: String?= "",
    var deliveryCharges: String?= "",
    var enquiryId: String?= "",
    var finalTotalAmt: Long?=0L,
    var ppu: Long?=0L,
    var quantity: Long?=0L,
    var sgst: String?=""
)


