package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AdminProductCatalogue : RealmObject() {
    @PrimaryKey
    var _id: Long? = 0
    var id: Long? = 0
    var images: String? = ""
    var code: String? = ""
    var name: String? = ""
    var productID: Long? = 0
    var icon: Long? = 0
    var availability: String? = ""
    var category: String? = ""
    var dateAdded: String? = ""
    var brand: String? = ""
    var noOfOrdersGenerated: Long? = 0
    var orderGenerated: Long? = 0
    var count: Long? = 0
    var isArtisan: Long? = 0
    var clusterName: String? = ""

    companion object {
        const val COLUMN_TABLE = "AdminProductCatalogue"
        const val COLUMN__ID = "_id"
        const val COLUMN_ID = "id"
        const val COLUMN_IS_ARTISAN = "icon"
        const val COLUMN_CODE = "code"
        const val COLUMN_NAME = "name"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_BRAND = "brand"
        const val COLUMN_AVAILABILITY = "availability"
        const val COLUMN_PRODUCT_ID = "productID"
        const val COLUMN_CLUSTER = "clusterName"
        const val COLUMN_DATE_ADDED = "dateAdded"
    }

}
