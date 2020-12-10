package com.adrosonic.craftexchange.repository.data.request.taxInv

import java.io.Serializable

open class SendTiRequest : Serializable{
    var advancePaidAmt: String?= ""
    var cgst: String?= ""
    var deliveryCharges: String?= ""
    var enquiryId: String?= ""
    var finalTotalAmt: Double?=0.0
    var ppu: Long?=0L
    var quantity: Long?=0L
    var sgst: String?=""
}


