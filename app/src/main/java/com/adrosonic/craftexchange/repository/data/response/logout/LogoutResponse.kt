package com.adrosonic.craftexchange.repository.data.response.logout


data class LogoutResponse (
    val timestamp: String,
    val status: Long,
    val error: String,
    val message: String,
    val path: String
)
