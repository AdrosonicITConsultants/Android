package com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue

import com.google.gson.annotations.SerializedName


data class ClusterProductDetailResponse (
    val data: ClusterData,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class ClusterData (
    val products: List<Product>,
    val cluster: Cluster
)


