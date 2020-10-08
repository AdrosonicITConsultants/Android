package com.adrosonic.craftexchangemarketing.repository.data.model

data class OtpVerifyModel (
    var email : String ,
    var otp : String,
    var roleId : Long
)