package com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry

data class GenerateEnquiryResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
    val enquiry: Enquiry,
    val productName: String,
    val ifExists: Boolean,
    //for existing enquiry..resuing same class
    val code: String,
    val enquiryId: Long
)

data class Enquiry (
    val id: Long,
    val code: String,
    val generatedBy: Long,
    val productId: Long,
    val customProductId: Long,
    val enquiryStatus: Long,
    val artisanId: Long,
    val moqId: Long,
    val piId: Long,
    val enquiryOrderStageId: Long,
    val createdOn: String,
    val modifiedOn: String
)
