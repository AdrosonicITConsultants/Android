package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CompletedEnquiries : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var enquiryID: Long? = 0
    var enquiryCode: String? = ""
    var productID: Long?  = 0
    var productName: String? = ""
    var productCode: String? = ""
    var enquiryStageID: Long?  = 0
    var enquiryStatusID: Long?  = 0  //2 -> open     1-> close
    var innerEnquiryStageID: Long?  = 0
    var productStatusID: Long?  = 0

    var orderCode: String?  = ""
    var orderCreatedOn: String? = ""
    var expectedDate: String? = ""
    var startedOn: String? = ""
    var lastUpdated: String? = ""
    var totalAmount: String? = ""

    var isMoqSend: Long?  = 0
    var isPiSend: Long?  = 0
    var isMoqRejected: Long?  = 0
    var changeRequestOn: Long?  = 0
    var isBlue: Long?  = 0

    var productType: String? = ""
    var productCategoryID: Long?  = 0
    var warpYarnID: Long?  = 0
    var weftYarnID: Long?  = 0
    var extraWeftYarnID: Long?  = 0
    var productImages: String? = ""
    var madeWithAnthran: Long?  = 0
    var brandName: String? = ""
    var ProductBrandName: String? = "" // TODO : to be changed

    var historyProductID: Long? = 0
    var productHistoryCode: String? = ""
    var productHistoryName: String? = ""
    var productCategoryHistoryID: Long? = 0
    var productHistoryImages: String? = ""
    var warpYarnHistoryID: Long? = 0
    var weftYarnHistoryID: Long? = 0
    var extraWeftYarnHistoryID: Long? = 0
    var productStatusHistoryID: Long? = 0
    var madeWithAnthranHistory: Long? = 0

    var userId: Long ?= 0
    var clusterName: String ?= ""
    var firstName: String? = ""
    var lastName: String? = ""
    var alternateMobile: String? = ""
    var profileImage: String ?= ""
    var brandDesc: String ?= ""
    var logo: String? = ""
    //    var companyName: String? = "" // TODO : Uncomment after issue fixed (company name)
    var city: String? = ""
    var district: String? = ""
    var pincode: String? = ""
    var line1: String? = ""
    var line2: String? = ""
    var street: String? = ""
    var state: String? = ""
    var country: String? = ""
    var email: String? = ""
    var mobile: String? = ""
    var pocFirstName: String? = ""
    var pocLastName: String? = ""
    var pocEmail: String? = ""
    var pocContact: String? = ""
    var gst: String? = ""

    companion object{
        const val COLUMN_ENQUIRY_ID = "enquiryID"
        const val COLUMN_PRODUCT_ID = "productID"
        const val COLUMN_ENQUIRY_CODE = "enquiryCode"
        const val COLUMN_LAST_UPDATED = "lastUpdated"

    }
}