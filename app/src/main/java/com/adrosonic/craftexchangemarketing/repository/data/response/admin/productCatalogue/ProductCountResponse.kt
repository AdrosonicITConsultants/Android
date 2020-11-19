package com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue

 data class ProductCountResponse (
val data: Long,
val valid: Boolean,
val errorMessage: String? = null,
val errorCode: Long
)