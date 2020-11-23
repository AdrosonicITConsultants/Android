package com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase

data class EnqiuiryListResponse(
    var data: ArrayList<EnquiryData>,
    var errorCode: Int,
    var errorMessage: Any,
    var valid: Boolean
)
data class EnquiryData(
    val currenStage: String,
    val code: String,
    val eId: Long,
    val innerCurrenStage : String?,
    val currenStageId: Int,
    val innerCurrenStageId: Int?,
    val productId: Int?,
    val cluster : String?,
    val amount : Double?,
    val productStatus: Int,
    val dateStarted: String,
    val lastUpdated: String,
    val eta: String,
    val tag: String?,
    val artisanBrand: String?,
    val buyerBrand: String?,
    val madeWithAntharan: Int?,
    val customProductId: Int?,
    val productHistoryId: Int?,
    val customProductHistoryId: Long?,
    val historyTag: String?,
    val productHistoryStatus : Int?,
    val buyerId : Long?,
    val artisanId : Long?
)