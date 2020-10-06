package com.adrosonic.craftexchangemarketing.repository.data.response.moq


data class MoqDeliveryTimesResponse (
    val data: List<Datum>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Datum (
    val id: Long,
    val days: Long,
    val deliveryDesc: String
)
