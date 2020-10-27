package com.adrosonic.craftexchange.repository.data.response.qc

data class SaveSendQcResponse (
    val data: List<ArtBuyQcResponse>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)
