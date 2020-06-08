package com.adrosonic.craftexchange.repository.data.model

data class OtpVerifyModel (
    var email : String ,
    var otp : String,
    var roleId : Long
)