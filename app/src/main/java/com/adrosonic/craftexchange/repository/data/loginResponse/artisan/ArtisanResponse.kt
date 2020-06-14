package com.adrosonic.craftexchange.repository.data.loginResponse.artisan

data class ArtisanResponse(
    var data: Data,
    var errorCode: Int ,
    var errorMessage: String ,
    var valid: Boolean
)

data class Cluster(
    var desc: String ,
    var id: Int
)
class Country(
    var id: Int ,
    var name: String
)
data class Data(
    var acctoken: String,
    var user: User
)

data class User(
    var accountNonExpired: Boolean ?= false,
    var accountNonLocked: Boolean,
    var addressses: List<Addressse>,
    var alternateMobile: String,
    var authorities: String,
    var cluster: Cluster,
    var companyDetails: String,
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
    var pointOfContact: String,
    var refRoleId: Int,
    var registeredOn: String,
    var socialMediaLink: String,
    var status: Int,
    var username: String,
    var weaverDetails: String,
    var websiteLink: String
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