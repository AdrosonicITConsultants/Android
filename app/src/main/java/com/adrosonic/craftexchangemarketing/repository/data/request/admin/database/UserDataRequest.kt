package com.adrosonic.craftexchangemarketing.repository.data.request.admin.database

data class UserDataRequest (
    val clusterId : Int,
    val pageNo : Int,
    val rating : Int,
    val roleId : Int,
    val searchStr : String,
    val sortBy : String,
    val sortType : String
)