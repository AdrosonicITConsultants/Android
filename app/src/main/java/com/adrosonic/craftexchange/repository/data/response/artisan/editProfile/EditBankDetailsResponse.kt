package com.adrosonic.craftexchange.repository.data.response.artisan.editProfile


data class EditBankDetailsResponse (
    val data: List<Datum>,
    val valid: Boolean,
    val errorMessage:String,
    val errorCode: Long
)

data class Datum (
    val id: Long,
    val accNo_UPI_Mobile: String,
    val name: String,
    val bankName: String,
    val ifsc: String,
    val branch: String,
    val accountType: AccountType,
    val userID: Long
)

data class AccountType (
    val id: Long,
    val accountDesc: String
)