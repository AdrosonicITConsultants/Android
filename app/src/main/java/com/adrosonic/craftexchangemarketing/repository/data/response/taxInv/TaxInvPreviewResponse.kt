package com.adrosonic.craftexchangemarketing.repository.data.response.taxInv


data class TaxInvPreviewResponse (
    val data: String ?= "",
    val valid: Boolean ,
    val errorMessage: String,
    val errorCode: Long
)