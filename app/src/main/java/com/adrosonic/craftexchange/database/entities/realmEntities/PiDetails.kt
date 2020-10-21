package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PiDetails : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var piId: Long?=0
    var enquiryId: Long?=0
    var orderId: Long?=0
    var artisanId: Long?=0
    var date: String?=""
    var orderName: String?=""
    var productCode: String?=""
    var quantity: Long?=0
    var ppu: Long?=0
    var expectedDateOfDelivery: String?=""
    var totalAmount: Long?=0
    var hsn: Long?=0
    var cgst: Long?=0
    var sgst: Long?=0
    var isSend: Long?=0
    var createdOn: String?=""
    var modifiedOn: String?=""
    var parentId: Long? = 0
    var actionMarkPiForSave: Long? = 0
    var actionMarkPiForSend: Long? = 0
    var actionMarkPiForRevise: Long? = 0

    companion object {
        const val COLUMN_TABLE = "PiDetails"
        const val COLUMN__ID = "_id"
        const val COLUMN_PI_ID = "piId"
        const val COLUMN_ENQUIRY_ID = "enquiryId"
        const val COLUMN_ORDER_ID = "orderId"
        const val COLUMN_ARTISAN_ID = "artisanId"
        const val COLUMN_CGST = "cgst"
        const val COLUMN_SGST = "sgst"
        const val COLUMN_MARK_PI_FOR_SAVE = "actionMarkPiForSave"
        const val COLUMN_MARK_PI_FOR_SENDING = "actionMarkPiForSend"
        const val COLUMN_MARK_PI_FOR_REVISE = "actionMarkPiForRevise"
    }
}