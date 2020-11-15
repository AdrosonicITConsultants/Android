package com.adrosonic.craftexchange.repository.data.response.Notification

data class NotificationResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val getAllNotifications: List<GetAllNotification>,
    val count: Long
)

data class GetAllNotification (
    val productDesc: String,
    val createdOn: String,
    val notificationTypeId: Long,
    val seen: Long,
    val companyName: String,
    val code: String,
    val customProduct: String,
    val notificationId: Long,
    val details: String,
    val type: String
)
