package com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue

class CategoryProductsResponse

data class CategoryProductDetailsResponse (
    val data: CategoryData,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class CategoryData (
    val products: List<Product>,
    val productCategory: String
)