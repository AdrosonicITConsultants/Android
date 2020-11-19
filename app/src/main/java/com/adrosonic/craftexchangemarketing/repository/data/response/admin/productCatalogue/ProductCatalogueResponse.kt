package com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue

data class ProductCatalogueResponse (
    val data: List<Datum>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Datum (
    val id: Long,
    val images: String,
    val code: String,
    val name: String,
    val productID: Long,
    val icon: Long,
    val availability: String,
    val category: String,
    val dateAdded: String,
    val brand: String,
    val noOfOrdersGenerated: Long? = null,
    val orderGenerated: Any? = null,
    val count: Any? = null
)