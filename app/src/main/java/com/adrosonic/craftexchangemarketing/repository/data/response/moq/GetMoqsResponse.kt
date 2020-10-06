package com.adrosonic.craftexchangemarketing.repository.data.response.moq

data class GetMoqsResponse (
    val data: List<Datum1>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Datum1 (
    val moq: Moq,
    val artisanId: Long,
    val enquiryId: Long,
    val brand: String,
    val logo: String,
    val clusterName: String,
    val state: String,
    val accepted: Boolean
)

//data class Moq (
//    val id: Long,
//    val moq: Long,
//    val ppu: String,
//    val deliveryTimeId: Long,
//    val isSend: Long,
//    val additionalInfo: String,
//    val createdOn: String,
//    val modifiedOn: String
//)


