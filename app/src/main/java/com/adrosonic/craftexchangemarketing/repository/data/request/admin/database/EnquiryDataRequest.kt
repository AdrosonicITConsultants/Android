package com.adrosonic.craftexchangemarketing.repository.data.request.admin.database

data class EnquiryDataRequest (
    val availability : Int,
    val buyerBrand : String?,
    val clusterId : Int,
    val enquiryId : String?,
    val fromDate : String,
    val madeWithAntaran : Int,
    val pageNo : Long,
    val productCategory : Int,
    val statusId : Int?,
    val toDate : String,
    val weaverIdOrBrand :String?
    )