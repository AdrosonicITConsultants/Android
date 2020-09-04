package com.adrosonic.craftexchange.repository.data.response.Notification

data class NotificationReadResponse (
    val data: String,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)