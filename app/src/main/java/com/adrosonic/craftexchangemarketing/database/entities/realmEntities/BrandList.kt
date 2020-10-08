package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class BrandList : RealmObject() {
    @PrimaryKey
    var _id : Long ?=0
    var artisanId: Long?= 0
    var clusterId: Long?= 0
    var firstName: String? = ""
    var companyName: String? = ""
    var profilePic: String? = ""
    var logo: String? = ""
    var productImages: String? = "" //TODO to be chnged
}