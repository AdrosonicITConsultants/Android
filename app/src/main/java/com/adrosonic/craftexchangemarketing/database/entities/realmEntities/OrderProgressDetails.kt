package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class OrderProgressDetails : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0

    var orderProgressID: Long ?= 0
    var enquiryID: Long ?= 0
    var isFaulty: Long ?= null
    var brand: String?=""
    var totalAmount: Long?=0
    var productType: String ?= ""
    var isResolved: Long ?= null
    var isAutoClosed: Long ?= null
    var buyerReviewComment: String ?= ""
    var artisanReviewComment: String ?= ""
    var artisanReviewId: String ?= ""
    var buyerReviewId: String ?= ""
    var orderDispatchDate: String ?= ""
    var orderReceiveDate: String ?= ""
    var orderCompleteDate: String ?= ""
    var orderDeliveryDate: String ?= ""
    var faultyMarkedOn: String ?= ""
    var isRefundReceived: Long ?= null
    var isProductReturned: Long ?= null
    var createdOn: String ?= ""
    var modifiedOn: String ?= ""
    var eta: String ?= ""
    var isPartialRefundReceived: Long ?= null
    var partialRefundInitializedOn: Long ?= 0
    var partialRefundReceivedOn: Long ?= 0

    companion object {
        const val COLUMN__ID = "_id"
        const val COLUMN_ENQUIRY_ID = "enquiryID"
    }
}