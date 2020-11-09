package com.adrosonic.craftexchange.repository.data.request.rating

data class RatingRequest(
    var enquiryId: Long,
    var givenBy: Long,
    var questionId: Long,
    var response: Double?,
    var responseComment: String?
)