package com.adrosonic.craftexchange.repository.data.response.search

data class SearchProductResponse (
    val data: List<SearchProductData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class SearchProductData (
    val status: Long,
    val madeWithAnthran: Long,
    val tag: String,
    val images: String,
    val productDesc: String,
    val id: Long
)