package com.adrosonic.craftexchange.repository.data.response.chat.escalations

data class EscalationCategoryResponse (
    val data: List<EscalationCategoryData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class EscalationCategoryData (
    val id: Long,
    val category: String
)
