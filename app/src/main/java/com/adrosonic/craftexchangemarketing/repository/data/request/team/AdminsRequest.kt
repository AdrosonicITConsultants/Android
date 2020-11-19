package com.adrosonic.craftexchangemarketing.repository.data.request.team

data class AdminsRequest(
    var pageNo: Int ?= 1,
    var refRoleId: Int ?= -1,  // -1 for All roles
    var searchStr: String ?= ""
)