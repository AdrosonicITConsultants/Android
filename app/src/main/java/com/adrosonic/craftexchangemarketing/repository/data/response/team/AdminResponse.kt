package com.adrosonic.craftexchangemarketing.repository.data.response.team

data class AdminResponse(
    val data: List<AdminProfileData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Int
)

data class AdminProfileData(
    val id: Int,
    val username: String,
    val memberSince: String,
    val role: String,
    val email: String,
    val mobile: String,
    val status: Int
)