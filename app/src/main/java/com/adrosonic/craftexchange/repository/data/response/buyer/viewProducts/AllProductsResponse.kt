package com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts


data class AllProductsResponse (
    val data: List<Product>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)
data class Product (
    val id: Long,
    val productDesc: String,
    var code : String,
    val productTypes: List<ProductType>
)

data class ProductType (
    val id: Long,
    val productDesc: String,
    val productCategoryId: Long?,
    val productLengths: List<ProductLength>,
    val productWidths: List<ProductWidth>,
    val relatedProductType: List<ProductType>
)
data class ProductLength (
    val id: Long,
    val length: String,
    val productTypeId: Long
)

data class ProductWidth (
    val id: Long,
    val width: String,
    val productTypeId: Long
)