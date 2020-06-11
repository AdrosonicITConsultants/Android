package com.adrosonic.craftexchange.repository.data.clusterResponse

data class ProductResponse(
    var data: List<Data>,
    var errorCode: Int,
    var errorMessage: Any,
    var valid: Boolean
)

data class Data(
    var clusterId: Int,
    var id: Int,
    var productCategory: ProductCategory
)

data class ProductCategory(
    var id: Int,
    var productDesc: String
)