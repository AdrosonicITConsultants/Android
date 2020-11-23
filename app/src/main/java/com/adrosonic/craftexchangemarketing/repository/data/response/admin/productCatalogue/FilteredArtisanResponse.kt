package com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue

data class FilteredArtisanResponse (
    val data: List<FilteredArtisans>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class FilteredArtisans (
    val cluster: String,
    val brand: String? = null,
    val rating: Any? = null,
    val weaverID: String,
    val status: Long,
    val email: String,
    val contact: String,
    val id: Long,
    val state: String
)