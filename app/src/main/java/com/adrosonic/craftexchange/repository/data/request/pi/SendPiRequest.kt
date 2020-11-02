package com.adrosonic.craftexchange.repository.data.request.pi

import java.io.Serializable


//data class SendPiRequest (
//    val cgst: Long,
//    val expectedDateOfDelivery: String,
//    val hsn: Long,
//    val ppu: Long,
//    val quantity: Long,
//    val sgst: Long
//)

open class SendPiRequest:Serializable {
    var cgst: Long?=0
    var expectedDateOfDelivery: String?=""
    var hsn: Long?=0
    var ppu: Long?=0
    var quantity: Long?=0
    var sgst: Long?=0
}

