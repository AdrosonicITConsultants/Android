package com.adrosonic.craftexchange.repository.data.request.pi


data class SendPiRequest (
    val cgst: Long,
    val expectedDateOfDelivery: String,
    val hsn: Long,
    val ppu: Long,
    val quantity: Long,
    val sgst: Long
)