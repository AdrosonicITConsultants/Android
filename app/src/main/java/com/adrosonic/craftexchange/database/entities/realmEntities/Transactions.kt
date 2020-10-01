package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Transactions : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var transactionID : Long ?= 0
    var enquiryID: Long ?= 0
    var enquiryCode: String? = ""
    var orderCode: String? = ""

    var piID: Long ?= 0
    var paymentID: Long ?= 0
    var taxInvoiceID: Long ?= 0
    var challanID: Long ?= 0

    var piHistoryID: Long ?= 0
    var receiptID: Long ?= 0
    var eta: String? = ""

    var percentage: Long ?= 0
    var paidAmount: Long ?= 0
    var totalAmount: Long ?= 0

    var accomplishedStatus: Long ?= 0
    var upcomingStatus: Long ?= 0
    var isActionCompleted: Long ?= 0
    var isActive: Long ?= 0

    var transactionOn: String ?= ""
    var completedOn: String ?= ""
    var modifiedOn: String ?= ""

    companion object{
        const val COLUMN_TRANSACTION_ID = "transactionID"
        const val COLUMN_ENQUIRY_ID = "enquiryID"
        const val COLUMN_PI_ID = "piID"
        const val COLUMN_PAYMENT_ID = "paymentID"
        const val COLUMN_TAXINVOICE_ID = "taxInvoiceID"
        const val COLUMN_CHALLAN_ID = "challanID"
        const val COLUMN_IS_ACTIVE = "isActive"
        const val COLUMN_MODIFIED_ON = "modifiedOn"
    }

}