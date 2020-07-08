package com.adrosonic.craftexchange.repository.data.registerResponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterResponse(

	@field:SerializedName("valid")
	val valid: Boolean? = null,

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("errorMessage")
	val errorMessage: String? = null,

	@field:SerializedName("errorCode")
	val errorCode: Int? = null
) : Parcelable