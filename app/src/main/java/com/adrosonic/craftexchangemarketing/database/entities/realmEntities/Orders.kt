package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import com.adrosonic.craftexchangemarketing.repository.data.response.orders.ProductType
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Orders:RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var comment: String? = ""
    var orderCreatedOn: String?=""
    var productId: Long?=0
    var companyName: String?=""
    var productCategoryId: Long?=0
    var mobile: String? = ""
    var profilePic: String? = ""
    var firstName: String? = ""
    var lastName:String? = ""
    var alternateMobile: String? = ""
    var totalAmount: Long?=0
    var orderCode: String? = ""
    var description: String? = ""
    var logo: String? = ""
    var warpYarnId: Long?=0
    var weftYarnId: Long?=0
    var extraWeftYarnId: Long?=0
    var email:  String? = ""
    var enquiryId: Long?=0
    var line1:  String? = ""
    var line2:  String? = ""
    var street:  String? = ""
    var city: String? = ""
    var pincode:  String? = ""
    var enquiryCode:  String? = ""
    var productStatusId: Long? = 0
    var productType:  String? = ""
    var district:  String? = ""
    var productName:  String? = ""
    var productCode:  String? = ""
    var productImages:  String? = ""
    var orderReceiveDate:  String? = ""
    var excpectedDate:  String? = ""
    var changeRequestModifiedOn:  String? = ""//date
    var innerEnquiryStageId: Long? = 0
    var enquiryStageId: Long?=0
    var startedOn: String?=""
    var changeRequestStatus: Long? = 0
    var pocFirstName:  String? = ""
    var productCategoryHistoryId: Long? = 0
    var warpYarnHistoryId: Long? = 0
    var weftYarnHistoryId: Long? = 0
    var extraWeftYarnHistoryId: Long? = 0
    var productStatusHistoryId: Long? = 0
    var madeWittAnthranHistory: Long? = 0
    var buyerRatingDone: Long? = 0
    var deliveryChallanUploaded: Long?=0
    var deliveryChallanLabel: String? = ""
    var historyProductId: Long? = 0
    var madeWittAnthran: Long? = 0
    var productHistoryImages: String? = ""
    var enquiryStatusId: Long?=0
    var pocLastName: String? = ""
    var pocEmail: String? = ""
    var pocContact: String? = ""
    var gst:String? = ""
    var productHistoryCode: String? = ""
    var productHistoryName: String? = ""
    var lastUpdated: String?=""
    var state: String? = ""
    var country: String? = ""
    var brandName: String?=""

    var artisanReviewId: Long ?= 0
    var isReprocess: Long?=0
    var isNewGenerated: Long?=0

    var isMoqSend: Long?  = 0
    var isPiSend: Long?  = 0
    var isMoqRejected: Long?  = 0
    var isPartialRefundReceived: Long?  = 0
    var isRefundReceived: Long?  = 0
    var isProductReturned: Long?  = 0
    var changeRequestOn: Long?  = 0
    var isBlue: Long?  = 0
    var userId: Long?= 0

    var isOrderFromCompleted:Long?=0
    var actionMarkCr:Long?=0
    var actionMarkCrStatusUpdate:Long?=0
    var crStatusUpdateInput:String?=""

    companion object{
        const val COLUMN__ID = "_id"
        const val COLUMN_ENQUIRY_ID = "enquiryId"
        const val COLUMN_PRODUCT_ID = "productId"
        const val COLUMN_ENQUIRY_CODE = "enquiryCode"
        const val COLUMN_ORDER_CODE = "orderCode"
        const val COLUMN_LAST_UPDATED = "lastUpdated"
        const val COLUMN_IS_COMPLETED = "isOrderFromCompleted"
        const val COLUMN_ACTION_MARK_CR = "actionMarkCr"
        const val COLUMN_ACTION_MARK_CR_STATUS_UPDATE = "actionMarkCrStatusUpdate"
        const val COLUMN_CR_STATUS_UPDATE_INPUT = "crStatusUpdateInput"
    }
}