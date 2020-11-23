package com.adrosonic.craftexchangemarketing.repository.data.response.Notification

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


    val code: String,
    val productDesc: String,
    val createdOn: String,
    val notificationType: String,
    val notificationId: Long
)
//"code": "E-W-D-SAR-2011",
//"productDesc": "Saree",
//"createdOn": "2020-11-06T00:30:01.000+0000",
//"notificationType": "Auto Escalation",
//"notificationId": 26852