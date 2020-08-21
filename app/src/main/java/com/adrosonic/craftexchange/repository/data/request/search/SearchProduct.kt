package com.adrosonic.craftexchange.repository.data.request.search

data class SearchProduct (
    val pageNo: Long,
    val searchString: String,
    val searchType: Long
    )
