package com.adrosonic.craftexchangemarketing.repository.data.response.chat

import com.google.gson.annotations.SerializedName


data class ChatListResponse (
    val data: List<ChatList>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)
data class ChatList (
    val enquiryNumber: String,
    val enquiryId: Long?,
    val productTypeId: String? = null,
    val buyerId: Long,
    val orderReceiveDate: String? = null,
    val lastChatDate: String,
    val orderAmount: Long? = null,
    val enquiryGeneratedOn: String,
    val convertedToOrderDate: String? = null,
    val escalation: Long,
    val orderStatus: String,
    val lastUpdatedOn: String,
    val orderStatusId: Long,
    val changeRequestDone: Long,
    val unreadMessage: Long,
    val buyerLogo: String,
    val buyerCompanyName: String

)