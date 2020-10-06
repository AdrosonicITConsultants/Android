package com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData

data class ProductUploadData (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val productCategories: List<ProductCategory>,
    val weaves: List<Weaf>,
    val yarns: List<Yarn>,
    val reedCounts: List<ReedCount>,
    val dyes: List<Dye>,
    val productCare: List<ProductCare>
)

data class Dye (
    val id: Long,
    val dyeDesc: String
)

data class ProductCare (
    val id: Long,
    val productCareDesc: String
)

data class ProductCategory (
    val id: Long,
    val productDesc: String,
    val code: String,
    val productTypes: List<ProductType>
)

data class ProductType (
    val id: Long,
    val productDesc: String,
    val productCategoryID: Long? = null,
    val productLengths: List<ProductLength>,
    val productWidths: List<ProductWidth>,
    val relatedProductType: List<ProductType>
)

data class ProductLength (
    val id: Long,
    val length: String,
    val productTypeID: Long
)

data class ProductWidth (
    val id: Long,
    val width: String,
    val productTypeID: Long
)

data class ReedCount (
    val id: Long,
    val count: String
)

data class Weaf (
    val id: Long,
    val weaveDesc: String
)

data class Yarn (
    val id: Long,
    val yarnDesc: String,
    val yarnType: YarnType
)

data class YarnType (
    val id: Long,
    val yarnTypeDesc: String,
    val yarnCounts: List<YarnCount>,
    val manual: Boolean
)

data class YarnCount (
    val id: Long,
    val count: String,
    val yarnTypeID: Long
)
