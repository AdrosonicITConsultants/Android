package com.adrosonic.craftexchange.repository.data.registerResponse

data class CountryResponse (
    val data: List<Datum>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Datum (
    val id: Long,
    val name: String
)
