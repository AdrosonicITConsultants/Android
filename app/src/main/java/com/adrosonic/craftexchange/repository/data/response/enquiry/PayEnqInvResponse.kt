package com.adrosonic.craftexchange.repository.data.response.enquiry

data class PayEnqInvResponse (
    val data: DetailsData,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class DetailsData (
    val enquiry: Enquiry,
    val payment: Payment,
    val pi: Pi,
    val invoice: Invoice,
    val challan: Challan,
    val taxInvoiceGenerated: Boolean,
    val deliveryChallanUploaded: Boolean
)

data class Challan (
    val id: Long,
    val enquiryId: Long,
    val status: Long,
    val createdOn: String,
    val modifiedOn: String,
    val orderDispatchDate: String,
    val eta: Any? = null
)

data class Enquiry (
    val id: Long,
    val code: String,
    val generatedBy: Long,
    val productID: Long,
    val customProductId: Any? = null,
    val enquiryStatus: Long,
    val isConvertedToOrder: Long,
    val orderCode: String,
    val artisanId: Long,
    val moqId: Long,
    val piId: Long,
    val enquiryOrderStageId: Long,
    val innerOrderStageId: Long,
    val productHistoryId: Any? = null,
    val customProductHistoryId: Any? = null,
    val createdOn: String,
    val modifiedOn: String,
    val orderCreatedOn: String,
    val isChangeRequestOn: Long,
    val lastUpdateOn: String,
    val lastChatDate: Any? = null,
    val chatArchive: Any? = null,
    val oldEnquiryId: Any? = null,
    val isReprocess: Any? = null,
    val isNewGenerated: Any? = null,
    val hibernateLazyInitializer: HibernateLazyInitializer
)

class HibernateLazyInitializer()

data class Invoice (
    val id: Long,
    val enquiryId: Long,
    val hsn: Long,
    val ppu: Long,
    val quantity: Long,
    val piAmt: Long,
    val date: String,
    val artisanId: Long,
    val invoiceNo: String,
    val advancePaidAmt: Long,
    val deliveryCharges: Long,
    val cgst: Long,
    val sgst: Long,
    val finalTotalAmt: Long,
    val isactive: Long,
    val createdon: String,
    val modifiedon: String
)

data class Payment (
    val id: Long,
    val type: Long,
    val enquiryId: Long,
    val pid: Long,
    val invoiceId: Long,
    val percentage: Long,
    val paidAmount: Long,
    val totalAmount: Long,
    val receivedOn: String,
    val rejectedOn: Any? = null,
    val status: Long,
    val createdOn: String,
    val modifiedOn: String
)

data class Pi (
    val id: Long,
    val enquiryId: Long,
    val artisanId: Long,
    val date: String,
    val orderName: String,
    val productCode: String,
    val quantity: Long,
    val ppu: Long,
    val expectedDateOfDelivery: String,
    val totalAmount: Long,
    val hsn: Long,
    val cgst: Long,
    val sgst: Long,
    val isSend: Long,
    val isRevised: Any? = null,
    val createdOn: String,
    val modifiedOn: String,
    val parentID: Any? = null,
    val hibernateLazyInitializer: HibernateLazyInitializer
)