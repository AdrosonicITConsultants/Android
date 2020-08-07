package com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign


data class AddOwnDesignResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val id: Long,
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
    val gsm: String,
    val weight: String,
    val productSpec: String,
    val productWeaves: Any? = null,
    val createdOn: String,
    val modifiedOn: String,
    val relProduct: List<Any?>,
    val productImages: Any? = null,
    val buyerID: Long,
    val isDeleted: Long
)

data class Dye (
    val id: Long,
    val dyeDesc: String
)

class HibernateLazyInitializer()

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

data class ProductCategory (
    val id: Long,
    val productDesc: String,
    val code: String,
    val productTypes: List<ProductType>
)

data class ProductType (
    val id: Long,
    val productDesc: String,
    val productCategoryID: Long,
    val productLengths: List<ProductLength>,
    val productWidths: List<ProductWidth>,
    val relatedProductType: List<Any?>
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
