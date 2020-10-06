package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CraftAdmin : RealmObject() {
    @PrimaryKey
    var _id: Long ?=0
    var id: Long ?=0
    var acctoken: String ?=""
    var username: String ?=""
    var email: String ?=""
    var refMarketingRoleId: Long ?=0
    var statusId: Int ?=0
    var createdOn: String ?=""
    var modifiedOn: String ?=""
    var enabled: Boolean ?= false
    var authorities: String ?=""
    var accountNonExpired: Boolean ?=false
    var accountNonLocked: Boolean ?=false
    var credentialsNonExpired: Boolean ?=false


}