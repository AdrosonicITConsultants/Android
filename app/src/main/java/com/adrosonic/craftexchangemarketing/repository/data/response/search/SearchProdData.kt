package com.adrosonic.craftexchangemarketing.repository.data.response.search

data class SearchProdData (
    val data: SearchData,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class SearchData (
    val id: Long,
    val code: String?,
    val tag: String?,
    val productCategory: ProductCategory?,
    val productType: ProductType,
    val warpYarn: Yarn?,
    val weftYarn: Yarn?,
    val extraWeftYarn: Yarn?,
    val warpYarnCount: String?,
    val weftYarnCount: String?,
    val extraWeftYarnCount: String?,
    val warpDye: Dye?,
    val weftDye: Dye?,
    val extraWeftDye: Dye?,
    val length: String,
    val width: String,
    val reedCount: ReedCount?,
    val productStatusId: Long?,
    val gsm: String?,
    val weight: String?,
    val productSpe: String?,
    val productCares: List<ProductCare>,
    val productWeaves: List<ProductWeaf>,
    val createdOn: String?,
    val modifiedOn: String?,
    val relProduct: List<SearchData>,
    val productImages: List<ProductImage>,
    val artitionId: Long?,
    val clusterId: Long?,
    val productCategoryDesc: String?,
    val productTypeDesc: String?,
    val clusterName: String?,
    val artistName: String?,
    val brand: String?,
    val madeWithAnthran: Long?,
    val isDeleted: Long?,
    val deletedByDeactivation: String?
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
