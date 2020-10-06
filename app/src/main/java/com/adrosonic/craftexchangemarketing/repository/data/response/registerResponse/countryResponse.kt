package com.adrosonic.craftexchangemarketing.repository.data.registerResponse

data class CountryResponse (
    val data: List<Country>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Country (
    val id: Long,
    val name: String
)
