package com.adrosonic.craftexchangemarketing.repository.data.response.chat.escalations

data class EscalationSummResponse (
    val data: List<EscSumData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class EscSumData (
    val id: Long,
    val enquiryId: Long,
    val escalationFrom: Long,
    val escalationTo: Long,
    val category: Long,
    val text: String,
    val isManual: Long,
    val escalationToadmin: Long,
    val escalationToadminDatetime: String,
    val isResolve: Long,
    val resolvedOn: String,
    val autoEscalationTypeId: Long,
    val createdOn: String,
    val modifiedOn: String
)
