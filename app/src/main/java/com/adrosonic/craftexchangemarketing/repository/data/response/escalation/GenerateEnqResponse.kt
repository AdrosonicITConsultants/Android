package com.adrosonic.craftexchangemarketing.repository.data.response.escalation

data class GenerateEnqResponse (
    val data: GeneratedEnq,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class GeneratedEnq (
    val id: Long,
    val code: String,
    val generatedBy: Long,
    val productID: Any? = null,
    val customProductID: Long,
    val enquiryStatus: Long,
    val isConvertedToOrder: Any? = null,
    val orderCode: Any? = null,
    val artisanID: Any? = null,
    val moqID: Any? = null,
    val piID: Any? = null,
    val enquiryOrderStageID: Long,
    val innerOrderStageID: Any? = null,
    val productHistoryID: Any? = null,
    val customProductHistoryID: Any? = null,
    val createdOn: String,
    val modifiedOn: String,
    val orderCreatedOn: Any? = null,
    val isChangeRequestOn: Any? = null,
    val lastUpdateOn: Any? = null,
    val lastChatDate: Any? = null,
    val chatArchive: Any? = null,
    val oldEnquiryID: Long,
    val isReprocess: Any? = null,
    val isNewGenerated: Any? = null
)