package com.adrosonic.craftexchange.repository.data.clusterResponse

data class CLusterResponse (
    val data: List<Cluster>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Cluster (
    val id: Long,
    val desc: String
)