package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductDimens : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var prodTypeId : Long?=0
    var productType : String ?=""
    var lengthId : Long ?=0
    var length : String?=""
    var widthId : Long ?=0
    var width : String?=""
}