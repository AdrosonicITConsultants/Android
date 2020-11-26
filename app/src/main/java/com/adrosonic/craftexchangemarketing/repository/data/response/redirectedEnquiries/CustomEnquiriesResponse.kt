package com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries

data class CustomEnquiriesResponse (
    val data: List<CustomEnquiries>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class CustomEnquiries (
    val id: Long,
    val code: String,
    val productCategory: String,
    val weave: String,
    val companyName: String,
    val date: String,
    val productId: Long
)
