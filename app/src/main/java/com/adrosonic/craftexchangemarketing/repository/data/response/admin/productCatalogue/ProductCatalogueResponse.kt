package com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue

data class ProductCatalogueResponse (
    val data: List<ProductCatalogueRes>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class ProductCatalogueRes (
    val id: Long,
    val images: String?= null,
    val code: String,
    val name: String,
    val productId: Long,
    val icon: Long,
    val availability: String,
    val category: String,
    val dateAdded: String,
    val brand: String?=null,
    val noOfOrdersGenerated: Long? = null,
    val orderGenerated: Long,
    val count: Long,
    val clusterName: String
)

