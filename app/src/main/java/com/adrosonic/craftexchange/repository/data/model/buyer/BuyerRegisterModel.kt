package com.adrosonic.craftexchange.repository.data.model.buyer

data class User (
    val address: Address,
    val alternateMobile: String,
    val companyDetails: CompanyDetails,
    val buyerPointOfContact: BuyerPointOfContact,
//    val clusterId: Long,     //not needed for buyer
    val designation: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val mobile: String,
    val pancard: String,
    val password: String,
//    val productCategoryIds: List<Long>,     //not needed for buyer
    val refRoleId: Long,
    val socialMediaLink: String,
//    val weaverId: String,   //not needed for buyer
    val websiteLink: String
)

data class Address (
    val addressType: AddressType,
    val city: String,
    val country: Country,
    val district: String,
    val id: Long,
    val landmark: String,
    val line1: String,
    val line2: String,
    val pincode: String,
    val state: String,
    val street: String
//    val userId: Long        //not needed for buyer
)

data class AddressType (
    val addrType: String,
    val id: Long?
)

data class Country (
    val id: Long
//    val name: String
)

data class CompanyDetails (
    val cin: String,
    val companyName: String,
    val contact: String,
    val gstNo: String,
    val id: Long,
    val logo: String
)

data class BuyerPointOfContact (
    val contactNo: String,
    val email: String,
    val firstName: String,
    val id: Long,
    val lastName: String
)
