package com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries

data class ArtisanLT8Response (
    val data: List<ArtisanData>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class ArtisanData (
    val artisan: Artisan,
    val isMailSent: Long
)

data class Artisan (
    val cluster: String,
    val status: Long,
    val email: String,
    val brand: String? = null,
    val weaverID: String,
    val rating: Double,
    val contact: String,
    val id: Long,
    val state: String? = null
)