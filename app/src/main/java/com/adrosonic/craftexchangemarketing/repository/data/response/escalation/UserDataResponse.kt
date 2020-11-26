package com.adrosonic.craftexchangemarketing.repository.data.response.escalation

data class UserDataResponse (
    var data: ArrayList<userData>,
    var errorCode: Int,
    var errorMessage: Any,
    var valid: Boolean
        )

data class userData(
    var artisanMail : String,
    var buyerMail : String,
    var buyerContact : String,
    var buyerAlternateContact : String,
    var pocFirstName : String,
    var pocLastName : String,
    var pocEmail : String,
    var pocContact : String,
    var artisanContact : String
)