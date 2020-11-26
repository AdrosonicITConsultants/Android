package com.adrosonic.craftexchangemarketing.repository.data.response.escalation


data class EscalationResponse(
    var data: ArrayList<EscalationData>,
    var errorCode: Int,
    var errorMessage: Any,
    var valid: Boolean
)

data class EscalationData(
    var date : String,
    var enquiryId : Long,
    var enquiryCode : String,
    var artistBrand : String,
    var buyerBrand : String,
    var cluster : String,
    var stage : String,
    var lastUpdated : String,
    var concern : String,
    var price : Long,
    var category : String,
    var raisedBy: Long,
    var escalationId : Long
)