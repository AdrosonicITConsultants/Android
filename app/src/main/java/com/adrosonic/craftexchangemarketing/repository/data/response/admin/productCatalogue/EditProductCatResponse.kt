package com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue


data class EditProductCatResponse (
    val data: Data1,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data1 (
    val id: Long,
    val code: String,
    val tag: String,
    val productCategoryId: Long,
    val productTypeId: Long,
    val warpYarnId: Long,
    val weftYarnId: Long,
    val extraWeftYarnId: Any? = null,
    val warpYarnCount: String,
    val weftYarnCount: String,
    val extraWeftYarnCount: Any? = null,
    val warpDyeId: Long,
    val weftDyeId: Long,
    val extraWeftDyeId: Any? = null,
    val length: String,
    val width: String,
    val reedCountId: Long,
    val productStatusId: Long,
    val gsm: String,
    val weight: String,
    val productSpec: String,
    val productCares: List<ProductCare>,
    val productWeaves: List<ProductWeaf>,
    val createdOn: String,
    val modifiedOn: String,
    val relProduct: List<Any?>,
    val productImages: List<ProductImage>,
    val artitionId: Long,
    val clusterId: Long,
    val marketingUserId: Long,
    val clusterName: String,
    val artistName: String,
    val brand: String,
    val madeWithAnthran: Long,
    val isDeleted: Long,
    val productCategoryDesc: String,
    val productTypeDesc: String
)

//data class ProductCare (
//    val id: Long,
//    val productId: Long,
//    val productCareId: Long
//)
//
//data class ProductImage (
//    val id: Long,
//    val lable: String,
//    val productId: Long
//)
//
//data class ProductWeaf (
//    val id: Long,
//    val productId: Long,
//    val weaveId: Long
//)
