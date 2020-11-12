package com.adrosonic.craftexchange.repository.data.request.search

data class SearchProduct (
    var pageNo: Long,
    var searchString: String,
    var searchType: Long,
    var madeWithAntaran : Long
)
