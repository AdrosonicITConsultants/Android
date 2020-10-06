package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductCares : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var productId : Long?=0
    var careId : Long?=0
    var productCareId : Long?=0     //TODO map with product care desc
}