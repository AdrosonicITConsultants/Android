package com.adrosonic.craftexchangemarketing.repository.data.resetResponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResetResponse(

	@field:SerializedName("valid")
	val valid: Boolean? = null,

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("errorMessage")
	val errorMessage: String? = null,

	@field:SerializedName("errorCode")
	val errorCode: Int? = null
) : Parcelable
