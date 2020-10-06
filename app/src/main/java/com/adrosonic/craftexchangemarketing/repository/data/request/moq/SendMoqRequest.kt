package com.adrosonic.craftexchangemarketing.repository.data.request.moq

data class SendMoqRequest (
    val additionalInfo: String,
    val deliveryTimeId: Long,
    val moq: Long,
    val ppu: String
)
