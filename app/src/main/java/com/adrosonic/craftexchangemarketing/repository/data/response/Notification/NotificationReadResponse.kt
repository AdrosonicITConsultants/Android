package com.adrosonic.craftexchangemarketing.repository.data.response.Notification

data class NotificationReadResponse (
    val data: String,
    val valid: Boolean,
    val errorMessage: String? = null,
    val errorCode: Long
)
