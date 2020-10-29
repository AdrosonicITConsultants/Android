package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchange.repository.data.resetResponse.ResetResponse
import com.adrosonic.craftexchange.repository.data.model.UserAuthModel
import retrofit2.Call
import retrofit2.http.*

interface ResetPasswordDao {

    @Headers("Accept: application/json")
    @POST("forgotpassword/resetpassword")
    fun resetPassword(@Header("Content-Type") headerValue:String,
                     @Body userAuthenticate : UserAuthModel
    ) : Call<ResetResponse>

    @Headers("Accept: application/json")
    @GET("forgotpassword/sendotp")
    fun sendOtp(@Query("email") email : String,
    @Query("roleId") roleId : Long) : Call<ResetResponse>

    @Headers("Accept: application/json")
    @POST("forgotpassword/verifyEmailOtp")
    fun verifyEmailOtp(@Header("Content-Type") headerValue:String,
                       @Body otpVerify : OtpVerifyModel
    ) : Call<ResetResponse>

}