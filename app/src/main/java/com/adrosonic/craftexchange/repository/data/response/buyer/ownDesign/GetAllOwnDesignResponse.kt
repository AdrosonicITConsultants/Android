package com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign

import com.adrosonic.craftexchange.repository.data.request.buyer.RelatedProduct

data class GetAllOwnDesignResponse (
    val data: List<OwnDesigns>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class OwnDesigns (
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
    val weight: String? = null,
    val product_spec: String,
    val productWeaves: List<ProductWeaf?>,
    val createdOn: String,
    val modifiedOn: String,
    val relProduct: List<RelatedProduct?>,
    val productImages: List<ProductImage>,
    val buyerId: Long,
    val isDeleted: Long
)
data class ProductWeaf (
    val id: Long,
    val productID: Long,
    val weaveID: Long
)

//data class Dye (
//    val id: Long,
//    val dyeDesc: DyeDesc
//)

//enum class DyeDesc {
//    AzoFreeReactiveDye,
//    NaturalDye
//}

//data class Yarn (
//    val id: Long,
//    val yarnDesc: String,
//    val yarnType: YarnType
//)
//
//data class YarnType (
//    val id: Long,
//    val yarnTypeDesc: YarnTypeDesc,
//    val yarnCounts: List<YarnCount>,
//    val manual: Boolean
//)
//
//data class YarnCount (
//    val id: Long,
//    val count: String,
//    val yarnTypeID: Long
//)



//data class ProductCategory (
//    val id: Long,
//    val productDesc: String,
//    val code: String,
//    val productTypes: List<ProductType>
//)

//data class ProductType (
//    val id: Long,
//    val productDesc: String,
//    val productCategoryID: Long,
//    val productLengths: List<ProductLength>,
//    val productWidths: List<ProductWidth>,
//    val relatedProductType: List<Any?>
//)

//data class ProductLength (
//    val id: Long,
//    val length: String,
//    val productTypeID: Long
//)
//
//data class ProductWidth (
//    val id: Long,
//    val width: String,
//    val productTypeID: Long
//)

data class ProductImage (
    val id: Long,
    val lable: String,
    val productId: Long
)

//data class ReedCount (
//    val id: Long,
//    val count: String
//)
