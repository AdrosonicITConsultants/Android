package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class EnquiryProductDetails : RealmObject() {
    @PrimaryKey
    var _id: Long? = 0
    var isCustom : Boolean ?= false
    //details of brand
    var artisanId: Long? = 0
    var artisanName: String? = ""
    var clusterId: Long? = 0
    var clusterName: String? = ""

    //product main
    var productId: Long? = 0
    var productCode: String? = ""
    var productTag: String? = ""

    //product catgory
    var productCategoryId: Long? = 0
    var productCategoryName : String? = ""
    var productCategoryCode : String? = ""

    //productType
    var productTypeId: Long? = 0
    var productTypeDesc: String? = ""
    var inProductCategory: Long? = 0

    //warp Yan
    var warpYarnId: Long? = 0
    var warpYarnDesc : String?= ""
    var warpYarnCount: String? = ""
    var warpDyeId: Long? = 0
    var warpDyeDesc: String?= ""

    //weft Yan
    var weftYarnId: Long? = 0
    var weftYarnDesc : String?=""
    var weftYarnCount: String? = ""
    var weftDyeId: Long? = 0
    var weftDyeDesc: String?= ""

    //extra weft Yan
    var extraWeftYarnId: Long? = 0
    var extraWeftYarnDesc : String?=""

    var extraWeftYarnCount: String? = ""
    var extraWeftDyeId: Long? = 0
    var extraWeftDyeDesc: String?= ""


    //product dimensions
    var productLength: String? = ""
    var productWidth: String? = ""

    //reed count
    var reedCountId: Long? = 0
    var reedCount: String? = ""

    //product status id ... 1-> MadeToOrder.... 2-> Available
    var productStatusId: Long? = 0

    //product specs
    var gsm: String? = ""
    var weight: String? = ""
    var product_spe: String? = ""

    //TODO : ProductCares, ProductImages and ProductWeaves...different table
    //TODO : Related Product Types to be implemented later

    var isDeleted: Long? = 0

    companion object {
        const val COLUMN_PRODUCT_ID = "productId"
        const val COLUMN_IS_CUSTOM = "isCustom"
        const val COLUMN__ID = "_id"
    }

}