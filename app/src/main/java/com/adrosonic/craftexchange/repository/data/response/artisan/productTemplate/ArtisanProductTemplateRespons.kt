package com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate


data class ArtisanProductTemplateRespons (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val id: Long,
    val code: String,
    val tag: String,
    val productCategory: ProductCategory,
    val productType: ProductType,
    val warpYarn: Yarn,
    val weftYarn: Yarn,
    val extraWeftYarn: Yarn,
    val warpYarnCount: String,
    val weftYarnCount: String,
    val extraWeftYarnCount: String,
    val warpDye: Dye,
    val weftDye: Dye,
    val extraWeftDye: Dye,
    val length: String,
    val width: String,
    val reedCount: ReedCount,
    val productStatusID: Long,
    val gsm: String,
    val weight: String,
    val productSpe: String,
    val productCares: Any? = null,
    val productWeaves: Any? = null,
    val createdOn: String,
    val modifiedOn: String,
    val relProduct: List<Any?>,
    val productImages: Any? = null,
    val artitionID: Long,
    val clusterID: Long,
    val productCategoryDesc: String,
    val productTypeDesc: String,
    val clusterName: String,
    val artistName: String,
    val brand: String,
    val madeWithAnthran: Long,
    val isDeleted: Long
)

data class Dye (
    val id: Long,
    val dyeDesc: String,
    val hibernateLazyInitializer: HibernateLazyInitializer
)

class HibernateLazyInitializer

data class Yarn (
    val id: Long,
    val yarnDesc: String,
    val yarnType: YarnType,
    val hibernateLazyInitializer: HibernateLazyInitializer
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

data class ProductCategory (
    val id: Long,
    val productDesc: String,
    val code: String,
    val productTypes: List<ProductType>,
    val hibernateLazyInitializer: HibernateLazyInitializer
)

data class ProductType (
    val id: Long,
    val productDesc: String,
    val productCategoryID: Long,
    val productLengths: List<ProductLength>,
    val productWidths: List<ProductWidth>,
    val relatedProductType: List<Any?>,
    val hibernateLazyInitializer: HibernateLazyInitializer? = null
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
    val count: String,
    val hibernateLazyInitializer: HibernateLazyInitializer
)
