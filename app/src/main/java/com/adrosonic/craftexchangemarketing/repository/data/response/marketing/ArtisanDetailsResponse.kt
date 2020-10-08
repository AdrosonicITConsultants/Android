package com.adrosonic.craftexchangemarketing.repository.data.response.marketing


data class ArtisanDetailsResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class Data (
    val id: Long,
    val email: String,
    val mobile: String,
    val alternateMobile: Any? = null,
    val firstName: String,
    val lastName: String,
    val deliveryAddress: Any? = null,
    val registeredAddress: RegisteredAddress,
    val weaverID: String,
    val companyDetails: CompanyDetails,
    val profilePic: String,
    val rating: String,
    val cluster: String,
    val productCategories: List<String>,
    val paymentAccountDetails: List<PaymentAccountDetail>,
    val poc: Any? = null,
    val websiteLink: Any? = null,
    val socialLink: Any? = null,
    val pancard: Any? = null
)

data class CompanyDetails (
    val id: Long,
    val companyName: String,
    val contact: Any? = null,
    val logo: String,
    val cin: Any? = null,
    val gstNo: Any? = null,
    val desc: String
)

data class PaymentAccountDetail (
    val id: Long,
    val accNoUPIMobile: String,
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

data class RegisteredAddress (
    val id: Long,
    val line1: String,
    val line2: Any? = null,
    val street: Any? = null,
    val city: Any? = null,
    val district: Any? = null,
    val state: Any? = null,
    val pincode: Any? = null,
    val landmark: Any? = null,
    val userID: Long,
    val addressType: AddressType,
    val country: Country
)

data class AddressType (
    val id: Long,
    val addrType: String
)

data class Country (
    val id: Long,
    val name: String
)
