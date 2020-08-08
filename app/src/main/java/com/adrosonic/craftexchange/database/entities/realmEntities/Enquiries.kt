package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Enquiries : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var id: Long ?=0
    var code: String ?=""
    var generatedBy: Long ?=0
    var productID: Long ?=0
    var productName: String ?=""
    var customProductID: Long ?=0
    var enquiryStatus: Long ?=0
    var artisanID: Long ?=0
    var moqID: Long ?=0
    var piID: Long ?=0
    var enquiryOrderStageID: Long ?=0
    var createdOn: String ?=""
    var modifiedOn: String ?=""
    var ifExists: Boolean ?=false

    companion object{
        const val COLUMN_ENQUIRY_ID = "id"
        const val COLUMN_PRODUCT_ID = "productID"
        const val COLUMN_IF_EXISTS = "ifExists"
    }

}