package com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase



data class UserProfileResponse (
    val data: Data,
    var valid: Boolean,
    var errorMessage: String,
    var errorCode: Long
)

data class Data (
     val id: Long,
     val email: String,
     val mobile: String,
     val alternateMobile: String,
     val firstName: String,
     val lastName: String,
     val deliveryAddress : Address,
     val registeredAddress: Address,
     val weaverId: String,
     val companyDetails: CompanyDetails,
     val profilePic: String,
     var rating: Float,
     val cluster: String,
     val productCategories: List<String>,
     val paymentAccountDetails: List<PaymentAccountDetail>,
     var poc: PointOfContact,
     val websiteLink: String,
     val socialMediaLink: String,
     val pancard: String,
     val designation: String,
     val status : Int

)


data class Address (
    val id: Long,
    val line1: String,
    val line2: String,
    val street: String,
    val city: String,
    val district: String,
    val state: String,
    val pincode: String,
    val landmark: String,
    val userId: Long,
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

data class CompanyDetails (
    val id: Long,
    val companyName: String,
    val contact: String,
    val logo: String,
    val cin: String,
    val gstNo: String,
    val desc: String
)

data class PaymentAccountDetail (
    val id: Long,
    val accNo_UPI_Mobile: String,
    val name: String,
    val bankName: String,
    val ifsc: String,
    val branch: String? = null,
    val accountType: AccountType,
    val userId: Long
)
data class AccountType (
    val id: Long,
    val accountDesc: String
)

data class PointOfContact(
    var contactNo: String,
    var email: String,
    var firstName: String,
    var id: Long,
    var lastName: String
)
