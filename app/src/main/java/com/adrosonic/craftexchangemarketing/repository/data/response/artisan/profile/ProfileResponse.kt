package com.adrosonic.craftexchangemarketing.repository.data.response.artisan.profile

data class ProfileResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
    val user: User,
    val address: List<Address>,
    val buyerCompanyDetails: CompanyDetails,
    val buyerPointOfContact: PointOfContact,
    val userProductCategories: List<UserProductCategory>
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

data class User (
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val designation: String,
    val mobile: String,
    val profilePic: String,
    val alternateMobile: String,
    val pancard: String,
    val websiteLink: String,
    val socialMediaLink: String,
    var pointOfContact: PointOfContact,
    val companyDetails: CompanyDetails,
    val weaverDetails: WeaverDetails,
    val cluster: Cluster,
    val refRoleID: Long,
    val registeredOn: String,
    val status: Int,
    val emailVerified: Int,
    val lastLoggedIn: String,
    val addressses: List<Address>,
    val rating: String,
    val paymentAccountDetails: List<PaymentAccountDetail>,
    val username: String,
    val enabled: Boolean,
    val authorities: String,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean
)

data class Cluster (
    val id: Long,
    val desc: String,
    val adjective: String
)

data class WeaverDetails (
    val id: Long,
    val weaverId: String
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

data class UserProductCategory (
    val id: Long,
    val userId: Long,
    val productCategoryId: Long
)
data class PointOfContact(
    var contactNo: String,
    var email: String,
    var firstName: String,
    var id: Long,
    var lastName: String
)
