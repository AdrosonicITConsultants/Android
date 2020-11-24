package com.adrosonic.craftexchangemarketing.repository.data.response.team

data class AdminsResponse(
    val data: List<AdminsData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Int
)

data class AdminsData(
    val id: Int,
    val username: String,
    val role: String,
    val email: String
)