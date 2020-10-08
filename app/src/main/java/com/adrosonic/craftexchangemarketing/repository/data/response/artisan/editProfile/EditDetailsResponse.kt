package com.adrosonic.craftexchangemarketing.repository.data.response.artisan.editProfile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditDetailsResponse(

    @field:SerializedName("valid")
    val valid: Boolean? = false,

    @field:SerializedName("data")
    val data: String? = "",

    @field:SerializedName("errorMessage")
    val errorMessage: String? = "",

    @field:SerializedName("errorCode")
    val errorCode: Int? = 0
) : Parcelable
