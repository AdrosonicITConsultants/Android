package com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.singleProduct



data class SingleProductDetails (
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
    val artitionID: Long,
    val clusterID: Long,
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
    val yarnTypeID: Long
)

data class ProductCare (
    val id: Long,
    val productID: Long,
    val productCareID: Long
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
    val width: Width,
    val productTypeID: Long
)

enum class Width {
    The46Inch,
    The48Inch
}

data class ProductImage (
    val id: Long,
    val lable: String,
    val productID: Long
)

data class ProductWeaf (
    val id: Long,
    val productID: Long,
    val weaveID: Long
)

data class ReedCount (
    val id: Long,
    val count: String
)
