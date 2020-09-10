package com.adrosonic.craftexchange.repository.data.request.moq

data class SendMoqRequest (
    val additionalInfo: String,
    val deliveryTimeId: Long,
    val moq: Long,
    val ppu: String
)
