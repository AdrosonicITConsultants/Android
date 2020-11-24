package com.adrosonic.craftexchangemarketing.repository.data.request.admin.database


data class CalogueRequest (
    val availability: Long,
    val clusterID: Long,
    val madeWithAntaran: Long,
    val pageNo: Long,
    val searchStr: String,
    val sortBy: String,
    val sortType: String
)
