package com.adrosonic.craftexchange.repository.data.loginResponse

data class LoginAuthResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)


 data class Data (
    val user: User,
    val acctoken: String
)

 data class User (
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val designation: String,
    val mobile: String,
    val alternateMobile: String,
    val pancard: String,
    val websiteLink: String,
    val socialMediaLink: String,
    val pointOfContact: PointOfContact,
    val buyerCompanyDetails: BuyerCompanyDetails,
    val weaverDetails: Any? = null,
    val cluster: Any? = null,
    val refRoleId: Long,
    val registeredOn: Any? = null,
    val status: Long,
    val emailVerified: Long,
    val lastLoggedIn: Any? = null,
    val addressses: List<Addresss>,
    val username: Any? = null,
    val enabled: Boolean,
    val authorities: Any? = null,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean
)

 data class Addresss (
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

 data class BuyerCompanyDetails (
    val id: Long,
    val companyName: String,
    val contact: String,
    val logo: String,
    val cin: String,
    val gstNo: String
)


 data class PointOfContact (
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val contactNo: String
)
