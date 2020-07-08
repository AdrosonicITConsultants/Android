package com.adrosonic.craftexchange.repository.data.model

data class UserAuthModel (
    var emailOrMobile : String,
    var password : String,
    var roleId : Long
)
