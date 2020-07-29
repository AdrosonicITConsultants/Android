package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductImages : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var productId : Long?=0
    var imageId : Long?=0
    var imageName : String?=""
}