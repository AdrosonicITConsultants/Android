package com.adrosonic.craftexchange.repository.data.response.artisan.products

data class ArtisanProductDetailsResponse (
    val data: List<Datum>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Datum (
    val productCategory: ProductCategory,
    val products: List<Product>
)

data class ProductCategory (
    val id: Long,
    val productDesc: String
)

data class Product (
    val id: Long,
    val code: String,
    val tag: String,
    val productCategoryId: Long,
    val productTypeId: Long,
    val warpYarnId: Long,
    val weftYarnId: Long,
    val extraWeftYarnId: Long,
    val warpYarnCount: String,
    val weftYarnCount: String,
    val extraWeftYarnCount: String,
    val warpDyeId: Long,
    val weftDyeId: Long,
    val extraWeftDyeId: Long,
    val length: String,
    val width: String,
    val reedCountId: Long,
    val productStatusId: Long,
    val gsm: String,
    val weight: String,
    val productSpec: String,
    val productCares: List<ProductCare>,
    val productWeaves: List<ProductWeaf>,
    val created_on: String,
    val modified_on: String,
    val relProduct: List<Product>,
    val productImages: List<ProductImage>,
    val artitionId: Long,
    val clusterId: Long,
    val isDeleted: Long,
    val productCategoryDesc: String,
    val productTypeDesc: String,
    val clusterName: String,
    val artistName: String?,
    val brand: String?,
    val madeWithAnthran: Long?
)

data class ProductCare (
    val id: Long,
    val productId: Long,
    val productCareId: Long
)

data class ProductImage (
    val id: Long,
    val lable: String,
    val productId: Long
)

data class ProductWeaf (
    val id: Long,
    val productId: Long,
    val weaveId: Long
)
