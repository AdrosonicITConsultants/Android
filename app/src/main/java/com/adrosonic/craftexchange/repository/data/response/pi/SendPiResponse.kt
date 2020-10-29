package com.adrosonic.craftexchange.repository.data.response.pi

data class SendPiResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val id: Long,
    val enquiryId: Long,
    val orderId: Long,
    val artisanId: Long,
    val date: String,
    val orderName: String,
    val productCode: String,
    val quantity: Long,
    val ppu: Long,
    val expectedDateOfDelivery: String,
    val totalAmount: Long,
    val hsn: Long,
    val cgst: Long,
    val sgst: Long,
    val isSend: Long,
    val createdOn: String,
    val modifiedOn: String,
    val parentId: Any? = null,
    val hibernateLazyInitializer: HibernateLazyInitializer
)

class HibernateLazyInitializer
