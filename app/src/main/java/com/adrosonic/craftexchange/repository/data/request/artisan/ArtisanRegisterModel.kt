package com.adrosonic.craftexchange.repository.data.request.artisan

data class User (
    val address: Address,
//    val alternateMobile: String,
//    val companyDetails: CompanyDetails,
//    val buyerPointOfContact: BuyerPointOfContact,
    val clusterId: Long,     //not needed for buyer
//    val designation: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val mobile: String,
    val pancard: String,
    val password: String,
    val productCategoryIds: List<Long>,
    val refRoleId: Long,
//    val socialMediaLink: String,
    val weaverId: String
//    val websiteLink: String
)

data class Address(
//    val addressType: AddressType,
    val country: Country,
    val district: String,
//    val id: Long,
    val line1: String,
    val pincode: String,
    val state: String
//    val userId: Long        //not needed for buyer
)

data class Country (
    val id: Long,
    val name: String
)
