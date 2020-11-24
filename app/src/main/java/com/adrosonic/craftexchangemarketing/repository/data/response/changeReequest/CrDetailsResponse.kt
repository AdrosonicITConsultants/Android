package com.adrosonic.craftexchangemarketing.repository.data.response.changeReequest

data class CrDetailsResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val changeRequest: ChangeRequest,
    val changeRequestItemList: List<ChangeRequestItemList>
)

data class ChangeRequest (
    val id: Long,
    val enquiryId: Long,
    val status: Long,
    val createdOn: String,
    val modifiedOn: String
)

data class ChangeRequestItemList (
    val id: Long,
    val changeRequestId: Long,
    val requestItemsId: Long,
    val requestText: String,
    val requestStatus: Long
)
