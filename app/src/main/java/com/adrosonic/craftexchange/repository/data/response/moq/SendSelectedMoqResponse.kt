package com.adrosonic.craftexchange.repository.data.response.moq

data class SendSelectedMoqResponse (
    val data: Data2,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data2 (
    val id: Long,
    val moq: Long,
    val ppu: String,
    val deliveryTimeID: Long,
    val isSend: Long,
    val additionalInfo: String,
    val createdOn: String,
    val modifiedOn: String
)
