package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RelatedProducts : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var inProductCategoryId : Long ?=0
    var relatedToProductId : Long ?=0
    var relatedProductId : Long ?=0
    var productTypeId : Long ?=0
    var productName : String ?=""
    var productLength : String ?=""
    var productWidth : String ?=""
    var productWeight : String ?=""
}