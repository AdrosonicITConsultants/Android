package com.adrosonic.craftexchangemarketing.repository.data.loginResponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginValidationResponse(

    @field:SerializedName("valid")
    var valid: Boolean? = null,

    @field:SerializedName("data")
    var data: String? = null,

    @field:SerializedName("errorMessage")
    var errorMessage: String? = null,

    @field:SerializedName("errorCode")
    var errorCode: Int? = null
) : Parcelable
