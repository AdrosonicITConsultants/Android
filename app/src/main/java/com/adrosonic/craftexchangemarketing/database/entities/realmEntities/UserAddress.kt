package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserAddress : RealmObject() {
    @PrimaryKey
    var _id: Long?=0
    var id : Long ?= 0
    var userid : Long?=0
    var addrtypeid : String ?=""
    var addrtype : String ?=""
    var country_id: String ?=""
    var country: String? = ""
    var pincode: String? = ""
    var city: String? = ""
    var street: String? = ""
    var district: String? = ""
    var state: String? = ""
    var landmark: String? = ""
    var line2: String? = ""
    var line1: String? = ""
}


