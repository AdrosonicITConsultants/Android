package com.adrosonic.craftexchangemarketing.repository.data.response.admin.login

import com.google.gson.JsonElement


data class AdminLoginResponse(
    var data: JsonElement,
    var errorCode: Int,
    var errorMessage: Any,
    var valid: Boolean
)

data class LoginData(
    var acctoken: String,
    var user: LoginUser
)

data class LoginUser(
    var id: Long,
    var username: String,
    var email: String,
    var mobile: String,
    var refMarketingRoleId: Long,
    var statusId: Int,
    var createdOn: String,
    var modifiedOn: String,
    var firstPasswordChanged: Boolean,
    var passwordExpiry: String,
    var resetPasswordToken: String,
    var failedAttempt: Int,
    var failedAttemptLockExpiry: String,
    var enabled: Boolean,
    var authorities: String,
    var accountNonExpired: Boolean,
    var accountNonLocked: Boolean,
    var credentialsNonExpired: Boolean
)
