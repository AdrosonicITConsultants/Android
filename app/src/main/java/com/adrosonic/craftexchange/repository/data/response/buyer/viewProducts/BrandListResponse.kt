package com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts

data class BrandListResponse (
    val data: List<BrandDetails>,
    val valid: Boolean,
    val errorMessage:String,
    val errorCode: Long
)

data class BrandDetails (
    val profilePic: String? = "",
    val companyName: String? = "",
    val firstName: String? = "",
    val logo: String? = "",
    val clusterId: Long?= 0,
    val productImages: String? = "", //TODO to be chnged
    val artisanId: Long?= 0
)