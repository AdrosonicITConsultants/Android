package com.adrosonic.craftexchangemarketing.repository.data.response.escalation

data class ResolvedEscalationResponse(
    var data: String,
    var valid: Boolean,
    var errorMessage: String,
    var errorCode: Long
)