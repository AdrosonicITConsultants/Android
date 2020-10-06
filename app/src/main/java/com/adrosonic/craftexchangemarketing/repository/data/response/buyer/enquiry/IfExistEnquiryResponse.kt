package com.adrosonic.craftexchangemarketing.repository.data.response.buyer.enquiry

data class IfExistEnquiryResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
    val code: String,
    val ifExists: Boolean,
    val productName: String,
    val enquiryId: Long
)
