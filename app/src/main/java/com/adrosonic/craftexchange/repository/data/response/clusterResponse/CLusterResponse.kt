package com.adrosonic.craftexchange.repository.data.response.clusterResponse

import io.realm.RealmObject

data class CLusterResponse (
    val data: List<Cluster>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Cluster(
    var id: Long ,
    var desc: String ,
    var adjective: String
)

