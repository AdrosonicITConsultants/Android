package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChangeRequests : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var crId: Long?=0
    var enquiryId: Long?=0
    var status: Long?=0
    var createdOn: String?=""
    var modifiedOn: String?=""
//    var crItemId: Long?=0
    var changeRequestId: Long?=0
    var requestItemsId: Long?=0
    var requestText: String?=""
    var requestStatus: Long?=0

    companion object {
        const val COLUMN_TABLE = "ChangeRequests"
        const val COLUMN__ID = "_id"
        const val COLUMN_CR_ID = "crId"
        const val COLUMN_ENQUIRY_ID = "enquiryId"
    }
}