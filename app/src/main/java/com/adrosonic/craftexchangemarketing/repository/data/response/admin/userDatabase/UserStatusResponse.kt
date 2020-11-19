package com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase


data class UserStatusResponse (
    val data: String,
    var valid: Boolean,
    var errorMessage: String,
    var errorCode: Long
)