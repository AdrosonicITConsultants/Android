package com.adrosonic.craftexchangemarketing.repository.data.request.editProfileModel

data class EditProfileDetails (
    val address: Address,
    val alternateMobile: String,
    val buyerCompanyDetails: BuyerCompanyDetails,
    val buyerPointOfContact: BuyerPointOfContact,
    val designation: String,
    val pancard: String
)

data class Address (
    val city: String,
    val country: Country,
    val district: String,
    val landmark: String,
    val line1: String,
    val line2: String,
    val pincode: String,
    val state: String,
    val street: String
)

data class Country (
    val id: Long
)

data class BuyerCompanyDetails (
    val cin: String,
    val companyName: String,
    val contact: String,
    val desc: String,
    val gstNo: String,
    val id: Long,
    val logo: String
)

data class BuyerPointOfContact(
    val contactNo: String,
    val email: String,
    val firstName: String,
    val id: Long?,
    val lastName: String
)