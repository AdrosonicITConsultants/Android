package com.adrosonic.craftexchange.repository.data.response.Notification

data class NotificationReadResponse (
    val data: String,
    val valid: Boolean,
    val errorMessage: String? = null,
    val errorCode: Long
)
//{"data":"saved","valid":true,"errorMessage":null,"errorCode":0}
//{"data":"Enquiry closed","valid":true,"errorMessage":null,"errorCode":0}
//{"data":"Status Updated!","valid":true,"errorMessage":null,"errorCode":0}
//{
//    "data": "PI Revised!",
//    "valid": true,
//    "errorMessage": null,
//    "errorCode": 0
//}

//{
//    "data": "Order Recieved",
//    "valid": true,
//    "errorMessage": null,
//    "errorCode": 0
//}