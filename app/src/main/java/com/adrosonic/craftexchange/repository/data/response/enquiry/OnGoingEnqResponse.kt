package com.adrosonic.craftexchange.repository.data.response.enquiry

data class OnGoingEnqResponse (
    val data: List<OngoingEnqData>,
    val valid: Boolean? = false,
    val errorMessage: String?= "",
    val errorCode: Long?= 0
)

data class OngoingEnqData (
    val openEnquiriesResponse: OpenEnquiriesResponse,
    val brandName: String?= "",
    val userId: Long ?= 0,
    val clusterName: String ?= "",
    val isMoqRejected: Long? = 0,
    val isBlue: Long? = 0,
    val paymentAccountDetails: List<PaymentAccountDetail>,
    val productCategories: List<ProductCategory>
)

data class OpenEnquiriesResponse (
    val productType: String ?= "",
    val productCode: String? = "",
    val orderCreatedOn:String? = "",
    val productName: String? = "",
    val excpectedDate: String? = "",
    val historyProductId: Long? = 0,
    val enquiryStageId: Long?= 0,
    val email: String? = "",
    val mobile: String? = "",
    val productCategoryId: Long? = 0,
    val warpYarnId: Long? = 0,
    val weftYarnId: Long? = 0,
    val extraWeftYarnId: Long? = 0,
    val firstName: String? = "",
    val lastName: String? = "",
    val alternateMobile: String? = "",
    val logo: String? = "",
    val description: String?= "",
    val profilePic: String?="",
    val companyName: String? = "",
    val totalAmount: String? = "",
    val orderCode: String? = "",
    val enquiryId: Long ?= 0,
    val productId: Long ?= 0,
    val productStatusId: Long? = 0,
    val productImages: String? = "",
    val city: String? = "",
    val district: String? = "",
    val pincode: String? = "",
    val line1: String? = "",
    val line2: String? = "",
    val street: String? = "",
    val startedOn: String? = "",
    val enquiryCode: String? = "",
    val lastUpdated: String? = "",
    val madeWittAnthran: Long ?= 0,
    val productHistoryImages: String? = "",
    val isMoqSend: Long? = 0,
    val isPiSend: Long? = 0,
    val enquiryStatusId: Long ?= 0,
    val pocFirstName: String? = "",
    val pocLastName: String? = "",
    val pocEmail: String? = "",
    val pocContact: String? = "",
    val gst: String? = "",
    val productHistoryCode: String? = "",
    val productHistoryName: String? = "",
    val productCategoryHistoryId: Long? = 0,
    val warpYarnHistoryId: Long? = 0,
    val weftYarnHistoryId: Long? = 0,
    val extraWeftYarnHistoryId: Long? = 0,
    val productStatusHistoryId: Long? = 0,
    val madeWittAnthranHistory: Long? = 0,
    val changeRequestOn:Long? = 0,
    val innerEnquiryStageId: Long? = 0,
    val state: String? = "",
    val country: String? = ""
)

data class PaymentAccountDetail (
    val id: Long?= 0,
    val accNo_UPI_Mobile: String,
    val name: String? = "",
    val bankName: String? = "",
    val ifsc: String? = "",
    val branch: String? = "",
    val accountType: AccountType,
    val userId: Long ?= 0
)

data class AccountType (
    val id: Long ?= 0,
    val accountDesc: String ?= ""
)

data class ProductCategory (
    val id: Long,
    val userId: Long,
    val productCategoryId: Long
)

