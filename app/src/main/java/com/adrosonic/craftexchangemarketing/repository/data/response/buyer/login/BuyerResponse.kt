package com.adrosonic.craftexchangemarketing.repository.data.response.buyer.login

data class BuyerResponse(
    var data: Data,
    var errorCode: Int,
    var errorMessage: Any,
    var valid: Boolean
)

data class Data(
    var acctoken: String,
    var user: User
)

data class User(
    var accountNonExpired: Boolean,
    var accountNonLocked: Boolean,
    var addressses: List<Addressse>,
    var alternateMobile: String,
    var authorities: String,
    var cluster: Cluster,
    var companyDetails: CompanyDetails,
    var credentialsNonExpired: Boolean,
    var designation: String,
    var email: String,
    var emailVerified: Int,
    var enabled: Boolean,
    var firstName: String,
    var id: Long,
    var lastLoggedIn: String,
    var lastName: String,
    var mobile: String,
    var pancard: String,
    val rating: String,
    var pointOfContact: PointOfContact,
    var refRoleId: Long,
    var registeredOn: String,
    var socialMediaLink: String,
    var status: Int,
    var username: String,
    var weaverDetails: String,
    var websiteLink: String
)

data class Cluster (
    val id: Long,
    val desc: String,
    val adjective: String
)

data class PointOfContact(
    var contactNo: String,
    var email: String,
    var firstName: String,
    var id: Long,
    var lastName: String
)

data class Country(
    var id: Int,
    var name: String
)

data class CompanyDetails(
    var cin: String,
    var companyName: String,
    var contact: String,
    var desc: String,
    var gstNo: String,
    var id: Long,
    var logo: String
)

data class AddressType(
    var addrType: String,
    var id: Int
)

data class Addressse(
    var addressType: AddressType,
    var city: String,
    var country: Country,
    var district: String,
    var id: Int,
    var landmark: String,
    var line1: String,
    var line2: String,
    var pincode: String,
    var state: String,
    var street: String,
    var userId: Int
)



