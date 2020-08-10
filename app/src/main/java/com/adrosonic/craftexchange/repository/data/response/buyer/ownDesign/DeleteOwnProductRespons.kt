package com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign

data class DeleteOwnProductRespons (
    val data: String,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)
