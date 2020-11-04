package com.adrosonic.craftexchange.repository.data.response.Rating

data class SendRatingResponse (
    val data : String,
    val valid: Boolean,
    val errorMessage: String? = null,
    val errorCode: Long
)