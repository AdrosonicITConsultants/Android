package com.adrosonic.craftexchangemarketing.repository.data.response.escalation


data class EscalationCountResponse(
    var data: Long,
    var valid: Boolean,
    var errorMessage: String,
    var errorCode: Long
)