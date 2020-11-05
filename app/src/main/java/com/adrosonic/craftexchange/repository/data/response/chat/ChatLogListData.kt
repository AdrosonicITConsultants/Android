package com.adrosonic.craftexchange.repository.data.response.chat

data class ChatLogListData (
    val data: List<ChatData>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class ChatData (
    val date: String,
    val chatBoxList: List<ChatBoxList>
)

data class ChatBoxList (
    val id: Long,
    val enquiryId: Long,
    val messageFrom: Int,
    val messageTo: Int,
    val messageString: String,
    val mediaType: Int,
    val mediaName: String,
    val path: String,
    val seen: Int,
    val isDelete: Int,
    val createdOn: String
)
