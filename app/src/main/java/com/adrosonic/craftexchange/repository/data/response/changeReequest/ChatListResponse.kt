package com.adrosonic.craftexchange.repository.data.response.changeReequest

data class ChatListResponse (
    val data: List<Chats>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Chats (
    val enquiryNumber: String,
    val enquiryID: Long,
    val productTypeID: String? = null,
    val buyerID: Long,
    val orderReceiveDate: String? = null,
    val lastChatDate: String,
    val orderAmount: Long? = null,
    val enquiryGeneratedOn: String,
    val convertedToOrderDate: String? = null,
    val escalation: Long,
    val orderStatus: String,
    val lastUpdatedOn: String,
    val orderStatusID: Long,
    val changeRequestDone: Long,
    val unreadMessage: Long,
    val buyerLogo: String,
    val buyerCompanyName: String
)