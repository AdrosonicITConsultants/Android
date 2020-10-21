package com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase

data class DatabaseCountResponse (
    var data: Int,
    var valid: Boolean,
    var errorMessage: String,
    var errorCode: Long
)