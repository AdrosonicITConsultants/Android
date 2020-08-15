package com.adrosonic.craftexchange.repository.data.response.search


data class SuggestionResponse (
    val data: List<Data>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
    val suggestion: String,
    val suggestionTypeId: Long,
    val suggestionType: String,
    val suggestionWithDetail: Long,
    val suggestionDetail: String,
    val plainSuggestion: Long,
    val id: Long
)