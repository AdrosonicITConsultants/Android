package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class BuyerCustomProduct : RealmObject() {
    @PrimaryKey
    var _id: Long? = 0
    var id: Long? = 0
    var productCategoryId:Long?=0
    var productCategoryDscrp:String?=""
    var productTypeId: Long? = 0
    var warpYarnId: Long? = 0
    var weftYarnId: Long? = 0
    var extraWeftYarnId: Long? = 0
    var warpYarnCount: String? = ""
    var weftYarnCount: String? = ""
    var extraWeftYarnCount: String? = ""
    var warpDyeId: Long? = 0
    var weftDyeId: Long? = 0
    var extraWeftDyeId: Long? = 0
    var length: String? = ""
    var width: String? = ""
    var reedCountId: Long? = 0
    var gsm: String? = ""
    var weight: String? = ""
    var productSpe: String? = ""
    var createdOn: String? = ""
    var modifiedOn: String? = ""
    var relatedProductId:String?="0"
    var weaveIds:String?="[]"
    var buyerId:Long?=0
    var isDeleted=0
    ////////////////////////////
    var dyeDsrcp=""
    var actionCreated=0
    var actionEdited=0
    var actionDelete=0

    companion object {
        const val COLUMN_TABLE = "BuyerCustomProduct"
        const val COLUMN_IS_DELETED = "isDeleted"
        const val COLUMN_ACTION_CREATED = "actionCreated"
        const val COLUMN_ACTION_DELETED = "actionDelete"
        const val COLUMN_ACTION_EDITED = "actionEdited"
        const val COLUMN_ID = "id"
        const val COLUMN__ID = "_id"
    }

}