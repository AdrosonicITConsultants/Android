package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductImages : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var productId : Long?=0
    var imageId : Long?=0
    var imageName : String?=""

    companion object{
        const val COLUMN_PRODUCT_ID="productId"
        const val COLUMN_IMAGE_ID="imageId"
        const val COLUMN_IMAGE_NAME="imageName"
    }
}