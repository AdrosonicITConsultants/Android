package com.adrosonic.craftexchange.repository.data.response.changeReequest

data class CrOptionsResponse (
    val data: List<CrOption>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class CrOption (
    val id: Long,
    val item: String
)