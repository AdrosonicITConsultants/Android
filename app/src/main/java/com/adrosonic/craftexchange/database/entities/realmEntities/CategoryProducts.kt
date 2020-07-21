package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CategoryProducts : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var productid : Long ?=0
    var code : String?=""
    var product : String?=""

    var subProductid : Long ?=0
    var subProduct : String ?=""
    var prodCategoryid : Long ?=0

}