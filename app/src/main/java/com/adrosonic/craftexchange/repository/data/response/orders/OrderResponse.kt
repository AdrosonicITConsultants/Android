package com.adrosonic.craftexchange.repository.data.response.orders

import com.adrosonic.craftexchange.repository.data.response.enquiry.PaymentAccountDetail

data class OrderResponse (
    val data: List<Datum>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Datum (
    val openEnquiriesResponse: OpenEnquiriesResponse,
    val brandName: String?="",
    val isMoqRejected: Long? = null,
    val isBlue: Long? = null,
    val userId: Long? = 0,
    val paymentAccountDetails: List<PaymentAccountDetail>
)


data class OpenEnquiriesResponse (
    val comment: String? = "",
    val orderCreatedOn: String,
    val productId: Long,
    val companyName: String?="",
    val productCategoryId: Long?=0,
    val mobile: String? = null,
    val profilePic: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val alternateMobile: String? = null,
    val totalAmount: Long,
    val orderCode: String,
    val description: String? = null,
    val logo: String? = null,
    val warpYarnId: Long,
    val weftYarnId: Long,
    val extraWeftYarnId: Long? = null,
    val email: String? = null,
    val enquiryId: Long,
    val line1: String? = null,
    val line2: String? = null,
    val street: String? = null,
    val city: String? = null,
    val pincode: String? = null,
    val enquiryCode: String,
    val productStatusId: Long? = null,
    val productType: String?="",
    val isPartialRefundReceived: Long? = null,
    val isRefundReceived: Long? = null,
    val isProductReturned: Long? = null,
    val district: String? = null,
    val productName: String,
    val productCode: String,
    val productImages: String,
    val orderReceiveDate: String? = null,
    val excpectedDate: String,
    val changeRequestModifiedOn: String? = null,
    val innerEnquiryStageId: Long? = null,
    val changeRequestOn: Long? = null,
    val enquiryStageId: Long,
    val startedOn: String,
    val changeRequestStatus: Long? = null,
    val pocFirstName: String? = null,
    val productCategoryHistoryId: Long? = null,
    val warpYarnHistoryId: Long? = null,
    val weftYarnHistoryId: Long? = null,
    val extraWeftYarnHistoryId: Long? = null,
    val productStatusHistoryId: Long? = null,
    val madeWittAnthranHistory: Long? = null,
    val buyerRatingDone: Long? = null,
    val deliveryChallanUploaded: Long,
    val deliveryChallanLabel: String? = null,
    val isPiSend: Long? = null,
    val historyProductId: Long? = null,
    val madeWittAnthran: Long? = null,
    val productHistoryImages: String? = null,
    val enquiryStatusId: Long,
    val pocLastName: String? = null,
    val pocEmail: String? = null,
    val pocContact: String? = null,
    val gst: String? = null,
    val productHistoryCode: String? = null,
    val productHistoryName: String? = null,
    val isMoqSend: Long? = null,
    val lastUpdated: String,
    val state: String? = null,
    val country: String? = null,
    val artisanReviewId: Long?=0,
    val isReprocess: Long?=0,
    val isNewGenerated: Long?=0,
    val revisedAdvancePaymentId: Long?=0
)

enum class ProductType {
    CustomProduct,
    Product
}
