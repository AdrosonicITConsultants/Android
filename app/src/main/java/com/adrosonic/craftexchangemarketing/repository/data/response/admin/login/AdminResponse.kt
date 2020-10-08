package com.adrosonic.craftexchangemarketing.repository.data.response.admin.login



data class AdminResponse(
    var data: Data,
    var errorCode: Int,
    var errorMessage: Any,
    var valid: Boolean
)
data class Data(
    var acctoken: String,
    var user: User
)


data class User(
    var id: Long,
    var username: String,
    var email: String,
    var refMarketingRoleId: Long,
    var statusId: Int,
    var createdOn: String,
    var modifiedOn: String,
    var enabled: Boolean,
    var authorities: String,
    var accountNonExpired: Boolean,
    var accountNonLocked: Boolean,
    var credentialsNonExpired: Boolean

)
