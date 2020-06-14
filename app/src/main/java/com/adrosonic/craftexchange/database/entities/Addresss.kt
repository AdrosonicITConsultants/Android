package com.adrosonic.craftexchange.database.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Addresss : RealmObject() {
    @PrimaryKey
    var _id: Long?=0
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


