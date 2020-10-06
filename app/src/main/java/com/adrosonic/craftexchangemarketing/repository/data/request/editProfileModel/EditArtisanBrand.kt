package com.adrosonic.craftexchangemarketing.repository.data.request.editProfileModel

data class EditArtisanBrand (
    val companyDetails: CompanyDetails,
    val productCategories: List<Long>
)

data class CompanyDetails (
    val companyName: String,
//    val contact: String? = null,
//    val cin: String? = null,
//    val gstNo: String? = null,
    val desc: String
)
