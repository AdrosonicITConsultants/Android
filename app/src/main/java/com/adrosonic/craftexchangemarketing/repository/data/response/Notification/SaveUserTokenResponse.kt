package com.adrosonic.craftexchangemarketing.repository.data.response.Notification

data class SaveUserTokenResponse (
    val data: Data1,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data1 (
    val id: Long,
    val userID: Long,
    val deviceID: Long,
    val deviceType: String,
    val token: String,
    val createdOn: String,
    val modifiedOn: String
)