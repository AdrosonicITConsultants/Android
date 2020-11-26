package com.adrosonic.craftexchangemarketing.repository.data.request.escalation

data class GetNewArtisanEnqRequest (
    val clusterId: Int,
    val enquiryId: Int,
    val searchString: String
)
//{
//    "clusterId": 0,
//    "enquiryId": 0,
//    "searchString": "string"
//}