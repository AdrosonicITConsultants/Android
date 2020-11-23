package com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate


data class ArtisanProductTemplateRespons (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val id: Long,
    val code: String? = null,
    val tag: String? = null,
    val productCategory: ProductCategory? = null,
    val productType: ProductType,
    val warpYarn: Yarn? = null,
    val weftYarn: Yarn? = null,
    val extraWeftYarn: Any? = null,
    val warpYarnCount: String? = null,
    val weftYarnCount: String? = null,
    val extraWeftYarnCount: Any? = null,
    val warpDye: Dye? = null,
    val weftDye: Dye? = null,
    val extraWeftDye: Any? = null,
    val length: String,
    val width: String
    ,
    val reedCount: ReedCount? = null,
    val productStatusID: Long? = null,
    val gsm: String? = null,
    val weight: String? = null,
    val productSpe: String? = null,
    val productCares: Any? = null,
    val productWeaves: Any? = null,
    val createdOn: String? = null,
    val modifiedOn: String? = null,
    val relProduct: List<Data>? = null,
    val productImages: Any? = null,
    val artitionID: Long,
    val clusterID: Long,
    val marketingUserID: Long? = null,
    val productCategoryDesc: String? = null,
    val productTypeDesc: String? = null,
    val clusterName: String? = null,
    val artistName: String? = null,
    val brand: String? = null,
    val madeWithAnthran: Long? = null,
    val isDeleted: Long? = null,
    val deletedByDeactivation: Any? = null
)

data class ProductCategory (
    val id: Long,
    val productDesc: String,
    val code: String,
    val productTypes: List<ProductType>,
    val hibernateLazyInitializer: HibernateLazyInitializer
)

class HibernateLazyInitializer()

data class ProductType (
    val id: Long,
    val productDesc: String,
    val productCategoryID: Long? = null,
    val productLengths: List<ProductLength>,
    val productWidths: List<ProductWidth>,
    val relatedProductType: List<ProductType>,
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

data class Dye (
    val id: Long,
    val dyeDesc: String,
    val hibernateLazyInitializer: HibernateLazyInitializer
)

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
