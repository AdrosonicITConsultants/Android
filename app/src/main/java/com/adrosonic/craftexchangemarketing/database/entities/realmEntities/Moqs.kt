package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Moqs : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var moqId: Long?=0
    var enquiryId: Long?=0
    var moq: Long?=0
    var ppu: String?=""
    var deliveryTimeId: Long?=0
    var isSend: Long?=0
    var additionalInfo: String?=""
    var createdOn: String?=""
    var modifiedOn: String?=""
    var accepted: Boolean?=false
    var actionMarkMoqForSend: Long?=0
    var artisanId: Long?=0
    var brand: String?=""
    var logo: String?=""
    var clusterName: String?=""
    var state: String?=""

    companion object {
        const val COLUMN_TABLE = "Moqs"
        const val COLUMN__ID = "_id"
        const val COLUMN_MOQ_ID = "moqId"
        const val COLUMN_ENQUIRY_ID = "enquiryId"
        const val COLUMN_PPU = "ppu"
        const val COLUMN_MOQ = "moq"
        const val COLUMN_DELIVERY_TIME_ID = "deliveryTimeId"
        const val COLUMN_MARK_MOQ_FOR_SENDING = "actionMarkMoqForSend"
    }
}