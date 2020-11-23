package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TaxInvDetails : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0

    var taxInvID: Long?=0
    var enquiryID: Long?=0
    var hsn: Long?=0
    var ppu: Long?=0
    var quantity: Long?=0
    var piAmt: Long?=0
    var date: String?=""
    var artisanID: Long?=0
    var invoiceNo: String?=""
    var advancePaidAmt: Long?=0
    var deliveryCharges: Long?=0
    var cgst: Long?=0
    var sgst: Long?=0
    var finalTotalAmt: Long?=0
    var isactive: Long?=null
    var createdon: String?=""
    var modifiedon: String?=""
    var actionMarkTiForSend: Long? = 0

    companion object {
        const val COLUMN_TABLE = "TaxInvDetails"
        const val COLUMN__ID = "_id"
        const val COLUMN_TI_ID = "taxInvID"
        const val COLUMN_ENQUIRY_ID = "enquiryID"
        const val COLUMN_MARK_TI_FOR_SENDING = "actionMarkTiForSend"
    }
}