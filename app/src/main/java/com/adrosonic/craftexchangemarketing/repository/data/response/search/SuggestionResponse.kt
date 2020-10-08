package com.adrosonic.craftexchangemarketing.repository.data.response.search


data class SuggestionResponse (
    val data: List<SuggData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class SuggData (
    val suggestion: String,
    val suggestionTypeId: Long,
    val suggestionType: String,
    val suggestionWithDetail: Long,
    val suggestionDetail: String,
    val plainSuggestion: Long,
    val id: Long
)