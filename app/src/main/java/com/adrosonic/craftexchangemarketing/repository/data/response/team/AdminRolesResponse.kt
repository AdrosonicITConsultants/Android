package com.adrosonic.craftexchangemarketing.repository.data.response.team

data class AdminRolesResponse(
    val data: List<AdminRoleData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Int
)


data class AdminRoleData(
    val id: Int,
    val desc: String
)