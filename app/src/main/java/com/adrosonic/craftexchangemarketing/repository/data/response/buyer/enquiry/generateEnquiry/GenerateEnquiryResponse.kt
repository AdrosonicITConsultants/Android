package com.adrosonic.craftexchangemarketing.repository.data.response.buyer.enquiry.generateEnquiry

data class GenerateEnquiryResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
    val enquiry: Enquiry,
    val productName: String ?= "",
    val ifExists: Boolean ?= false
)

data class Enquiry (
    val id: Long ?= 0,
    val code: String ?= "",
    val generatedBy: Long ?= 0,
    val productId: Long ?= 0,
    val customProductId:Long ?= 0,
    val enquiryStatus: Long ?= 0,
    val isConvertedToOrder: Long ?= 0,
    val orderCode: Long ?= 0,
    val artisanId: Long ?= 0,
    val moqId: Long ?= 0,
    val piId: Long ?= 0,
    val enquiryOrderStageId: Long ?= 0,
    val innerOrderStageId: Long ?= 0,
    val productHistoryId: Long ?= 0,
    val customProductHistoryId: Long ?= 0,
    val createdOn: String ?= "",
    val modifiedOn: String ?= "",
    val orderCreatedOn: String ?= "",
    val isChangeRequestOn: Long ?= 0,
    val lastUpdateOn: String ?= "",
    val lastChatDate: String ?= ""
)

