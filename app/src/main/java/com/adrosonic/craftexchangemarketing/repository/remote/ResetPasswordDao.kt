package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchangemarketing.repository.data.resetResponse.ResetResponse
import com.adrosonic.craftexchangemarketing.repository.data.model.UserAuthModel
import com.adrosonic.craftexchangemarketing.repository.data.request.authModel.AdminAuthModel
import com.adrosonic.craftexchangemarketing.repository.data.request.authModel.ResetPasswordModel
import retrofit2.Call
import retrofit2.http.*

interface ResetPasswordDao {

    @Headers("Accept: application/json")
    @POST("api/forgotpassword/resetMarketingPassword")
    fun resetPassword(@Header("Content-Type") headerValue:String,
                     @Body userAuthenticate : AdminAuthModel
    ) : Call<ResetResponse>

    @Headers("Accept: application/json")
    @GET("api/forgotpassword/sendOtpToMarketingTeam")
    fun sendOtp(@Query("username") username : String
    ) : Call<ResetResponse>

    @Headers("Accept: application/json")
    @POST("api/forgotpassword/verifyEmailOtp")
    fun verifyEmailOtp(@Header("Content-Type") headerValue:String,
                       @Body otpVerify : OtpVerifyModel
    ) : Call<ResetResponse>

    @Headers("Accept: application/json")
    @POST("api/forgotpassword/resetMarketingUserPassword")
    fun resetUserPassword(@Header("Content-Type") headerValue:String,
                      @Body userAuthenticate : ResetPasswordModel
    ) : Call<ResetResponse>

}