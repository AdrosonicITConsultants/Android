package com.adrosonic.craftexchangemarketing.repository.data.request.team

import java.io.Serializable

open class AdminsRequest : Serializable {
    var pageNo: Int ?= 1
    var refRoleId: Int ?= -1  // -1 for All roles
    var searchStr: String ?= null
}