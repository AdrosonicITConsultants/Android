package com.adrosonic.craftexchangemarketing.repository.data.request.editProfileModel


data class PaymentAccountDetails (
    val accNo_UPI_Mobile: String,
    val accountType: AccountType,
    val bankName: String,
    val branch: String,
    val id: Long,
    val ifsc: String,
    val name: String,
    val userId: Long
)

data class AccountType (
    val accountDesc: String,
    val id: Long
)