package com.adrosonic.craftexchangemarketing.repository.data.response.escalationa

data class GetNewArtisanEnqResponse (
    val data: List<ArtisanData1>,
    val valid: Boolean,
    val errorMessage: Any? = null,
    val errorCode: Long
)

data class ArtisanData1 (
    val artisan: Artisan,
    val isMailSent: Long
)

data class Artisan (
    val cluster: String,
    val status: Long,
    val rating: Double,
    val brand: String? = null,
    val email: String,
    val weaverID: String? = null,
    val contact: String,
    val id: Long,
    val state: String? = null
)
