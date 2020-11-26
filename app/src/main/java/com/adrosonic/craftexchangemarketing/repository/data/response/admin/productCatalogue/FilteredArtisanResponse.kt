package com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue

import io.realm.annotations.Ignore

data class FilteredArtisanResponse (
    val data: List<FilteredArtisans>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class FilteredArtisans (
    val cluster: String,
    val brand: String? = null,
    val rating: Long? = null,
    val weaverID: String,
    val status: Long,
    val email: String,
    val contact: String,
    val id: Long,
    val state: String,
    @Ignore
    var selectionStatus:Boolean=false
)