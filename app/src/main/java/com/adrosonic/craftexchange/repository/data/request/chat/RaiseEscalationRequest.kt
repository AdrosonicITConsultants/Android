package com.adrosonic.craftexchange.repository.data.request.chat

import com.adrosonic.craftexchange.repository.data.request.taxInv.DateeFormat

data class RaiseEscalationRequest (
    val autoEscalationTypeId: Long,
    val category: Long,
    val createdOn: DateeFormat,
    val enquiryId: Long,
    val escalationFrom: Long,
    val escalationTo: Long,
    val escalationToadmin: Long,
    val escalationToadminDatetime: DateeFormat,
    val id: Long,
    val isManual: Long,
    val isResolve: Long,
    val modifiedOn: DateeFormat,
    val resolvedOn: DateeFormat,
    val text: String
)
