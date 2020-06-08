package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.repository.data.model.User
import com.adrosonic.craftexchange.repository.data.registerResponse.CountryResponse
import retrofit2.Call
import retrofit2.http.*

interface RegisterDao {

    @Headers("Accept: application/json")
    @GET("register/sendVerifyEmailOtp")
    fun sendVerifyEmailOtp(@Query("email") email : String) : Call<RegisterResponse>

    @Headers("Accept: application/json")
    @POST("register/verifyEmailOtp")
    fun verifyEmailOtp(@Header("Content-Type") headerValue:String,
                        @Body otpVerify : OtpVerifyModel
    ) : Call<RegisterResponse>

    @Headers("Accept: application/json")
    @POST("register/user")
    fun registerBuyer(@Header("Content-Type") headerValue:String,
                      @Body registerRequest : User
    ): Call<RegisterResponse>

    @Headers("Accept: application/json")
    @GET("register/getAllCountries")
    fun getAllCountries() : Call<CountryResponse>
}