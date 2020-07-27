package com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue

data class BrandProductDetailResponse (
    val data: BrandData,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class BrandData (
    val products: List<Product>,
    val artisanDetails: List<ArtisanDetail>
)




