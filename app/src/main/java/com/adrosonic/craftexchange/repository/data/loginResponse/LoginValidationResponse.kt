package com.adrosonic.craftexchange.repository.data.loginResponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginValidationResponse(

    @field:SerializedName("valid")
    val valid: Boolean? = null,

    @field:SerializedName("data")
    val data: String? = null,

    @field:SerializedName("errorMessage")
    val errorMessage: String? = null,

    @field:SerializedName("errorCode")
    val errorCode: Int? = null
) : Parcelable
