package com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate

data class UpdateProductTemplateRequest(
    val code: String,
    val extraWeftDyeId: Long,
    val extraWeftYarnCount: String,
    val extraWeftYarnId: Long,
    val gsm: String,
    val id: Long,
    val length: String,
    val productCares: List<ProductCare>,
    val productCategoryId: Long,
    val productStatusId: Long,
    val productTypeId: Long,
    val productWeaves: List<ProductWeaf>,
    val productSpec: String,
    val reedCountId: Long,
    val relProduct: List<RelProduct>,
    val tag: String,
    val warpDyeId: Long,
    val warpYarnCount: String,
    val warpYarnId: Long,
    val weftDyeId: Long,
    val weftYarnCount: String,
    val weftYarnId: Long,
    val weight: String,
    val width: String
)

data class ProductCare(
    val id: Long,
    val productCareId: Long,
    val productId: Long
)

data class ProductWeaf(
    val id: Long,
    val productId: Long,
    val weaveId: Long
)

data class RelProduct(
    val productTypeId: Long,
    val width: String,
    val length: String
)
