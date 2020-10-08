package com.adrosonic.craftexchangemarketing.repository.data.request.buyer

data class UpdateOwnDesignRequest (
    val extraWeftDyeId: Long,
    val extraWeftYarnCount: String,
    val extraWeftYarnId: Long,
    val gsm: String,
    val id: Long,
    val length: String,
    val productCategoryId: Long,
    val productTypeId: Long,
    val productWeaves: List<ProductWeaf>,
    val productSpec: String,
    val reedCountId: Long,
    val relProduct: List<RelProduct>,
    val warpDyeId: Long,
    val warpYarnCount: String,
    val warpYarnId: Long,
    val weftDyeId: Long,
    val weftYarnCount: String,
    val weftYarnId: Long,
    val weight: String,
    val width: String
)

data class ProductWeaf (
    val id: Long,
    val productId: Long,
    val weaveId: Long
)

data class RelProduct (
    val productTypeId: Long,
    val width: String,
    val length: String
)
