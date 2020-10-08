package com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.singleProduct



data class SingleProductDetails (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
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
    val extraWeftYarn: Yarn? = null,
    val warpYarnCount: String? = null,
    val weftYarnCount: String? = null,
    val extraWeftYarnCount: String? = null,
    val warpDye: Dye? = null,
    val weftDye: Dye? = null,
    val extraWeftDye: Dye? = null,
    val length: String,
    val width: String,
    val reedCount: ReedCount? = null,
    val productStatusId: Long? = null,
    val gsm: String? = null,
    val weight: String? = null,
    val product_spe: String? = null,
    val productCares: List<ProductCare>,
    val productWeaves: List<ProductWeaf>,
    val createdOn: String? = null,
    val modifiedOn: String? = null,
    val relProduct: List<Data>,
    val productImages: List<ProductImage>,
    val artitionId: Long,
    val clusterId: Long,
    val productCategoryDesc: String? = null,
    val productTypeDesc: String? = null,
    val clusterName: String? = null,
    val artistName: String? = null,
    val brand: String? = null,
    val madeWithAnthran: Long? = null,
    val isDeleted: Long? = null
)

data class Dye (
    val id: Long,
    val dyeDesc: String
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
    val yarnTypeId: Long
)

data class ProductCare (
    val id: Long,
    val productId: Long,
    val productCareId: Long
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
    val productCategoryId: Long? = null,
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

data class ReedCount (
    val id: Long,
    val count: String
)
