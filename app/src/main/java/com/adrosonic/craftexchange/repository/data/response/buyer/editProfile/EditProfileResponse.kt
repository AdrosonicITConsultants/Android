package com.adrosonic.craftexchange.repository.data.editProfile

import com.adrosonic.craftexchange.repository.data.response.artisan.profile.AddressType
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.Cluster
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.Country

data class EditProfileResponse (
    val data: Data,
    val valid: Boolean,
    val errorMessage: String,
    val errorCode: Long
)

data class Data (
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
    val pointOfContact: PointOfContact,
    val companyDetails: CompanyDetails,
    val weaverDetails: WeaverDetails,
    val cluster: Cluster,
    val refRoleId: Long,
    val registeredOn: String? = null,
    val status: Int,
    val emailVerified: Int,
    val lastLoggedIn: String? = null,
    val addressses: List<Addresss>,
    val rating: String,
    val paymentAccountDetails: List<String?>,
    val enabled: Boolean,
    val authorities: String,
    val username: String,
    val accountNonExpired: Boolean,
    val credentialsNonExpired: Boolean,
    val accountNonLocked: Boolean
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

data class Cluster (
    val id: Long,
    val desc: String,
    val adjective: String
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

data class PointOfContact (
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val contactNo: String
)

data class WeaverDetails (
    val id: Long,
    val weaverID: String
)