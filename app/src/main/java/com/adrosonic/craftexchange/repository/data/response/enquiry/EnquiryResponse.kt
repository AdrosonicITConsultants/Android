package com.adrosonic.craftexchange.repository.data.response.enquiry

import com.google.gson.annotations.SerializedName


data class EnquiryResponse (
    val data: List<EnquiryData>,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class EnquiryData (
    val openEnquiriesResponse: EnquiriesResponse,
    val userId: Long?=0,
    val clusterName: String?="",
    val isBlue: Long?=0,
    val brandName : String ?= "",
    val isMoqRejected : Long?= 0,
    val paymentAccountDetails: List<PaymentAccountDetail>,
    val productCategories: List<ProductCategory>
)

data class EnquiriesResponse (
    val profilePic: String?="",
    val productCategoryId: Long?=0,
    val warpYarnId: Long?=0,
    val weftYarnId: Long?=0,
    val extraWeftYarnId: Long?=0,
    val description: String?="",
    val orderCode: String?="",
    val totalAmount: String?="",
    val enquiryId: Long?=0,
    val orderCreatedOn: String? = "",
    val productId: Long?=0,
    val companyName: String?="",
    val alternateMobile: String? ="",
    val logo: String?="",
    val email: String?="",
    val mobile: String?="",
    val firstName: String?="",
    val lastName: String?="",
    val productStatusId: Long?=0,
    val productType: String?="",
    val productImages: String?="",
    val line1: String?="",
    val line2: String?="",
    val street: String?="",
    val city: String?="",
    val district: String?="",
    val pincode: String?="",
    val productCode: String?="",
    val enquiryCode: String?="",
    val isMoqSend: Long?=0,
    val isPiSend: Long?=0,
    val pocFirstName: String?="",
    val pocLastName: String?="",
    val pocEmail: String?="",
    val pocContact: String?="",
    val gst:String?="",
    val productHistoryCode: String?="",
    val productHistoryName: String?="",
    val productCategoryHistoryId:Long?=0,
    val warpYarnHistoryId: Long?=0,
    val weftYarnHistoryId: Long?=0,
    val extraWeftYarnHistoryId: Long?=0,
    val productStatusHistoryId: Long?=0,
    val madeWittAnthranHistory: Long?=0,
    val startedOn: String?="",
    val lastUpdated: String?="",
    val enquiryStageId: Long?=0,
    val changeRequestOn: Long?=0,
    val changeRequestStatus: String?="",
    val changeRequestModifiedOn: String?="",
    val innerEnquiryStageId:Long?=0,
    val productName: String?="",
    val excpectedDate: String?="",
    val historyProductId: Long?=0,
    val madeWittAnthran: Long?=0,
    val productHistoryImages: String ?="",
    val enquiryStatusId: Long?=0,
    val state: String?="",
    val country: String?=""
)

data class PaymentAccountDetail (
    val id: Long?=0,
    val accNo_UPI_Mobile: String?="",
    val name: String?="",
    val bankName: String?="",
    val ifsc: String? ="",
    val branch: String? ="",
    val accountType: AccountType,
    val userId: Long?=0
)

data class AccountType (
    val id: Long?=0,
    val accountDesc: String ?=""
)

data class ProductCategory (
    val id: Long?=0,
    val userId: Long?=0,
    val productCategoryId: Long?=0
)




