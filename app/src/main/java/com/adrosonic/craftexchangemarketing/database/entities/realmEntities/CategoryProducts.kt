package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CategoryProducts : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var productCategoryid : Long ?=0
    var code : String?=""
    var product : String?=""

    var subProductid : Long ?=0
    var subProduct : String ?=""
    var inProductCategory : Long ?=0

}