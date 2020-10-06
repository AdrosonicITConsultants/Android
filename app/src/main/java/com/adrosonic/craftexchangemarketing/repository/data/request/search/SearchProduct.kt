package com.adrosonic.craftexchangemarketing.repository.data.request.search

data class SearchProduct (
    val pageNo: Long,
    val searchString: String,
    val searchType: Long
    )
