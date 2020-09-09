package com.adrosonic.craftexchange.repository.data.response.moq

data class SendMoqResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val moq: Moq,
    val accepted: Boolean
)

data class Moq (
    val id: Long,
    val moq: Long,
    val ppu: String,
    val deliveryTimeId: Long,
    val isSend: Long,
    val additionalInfo: String,
    val createdOn: String,
    val modifiedOn: String
)
