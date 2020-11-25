package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.model.artisan.ArtisanidModel
import com.adrosonic.craftexchange.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.repository.data.model.buyer.User
import com.adrosonic.craftexchange.repository.data.registerResponse.CountryResponse
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.Notification.SaveUserTokenResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface RegisterDao {

    @Headers("Accept: application/json")
    @POST("user/saveDeviceToken/{deviceId}/AND/{token}")
    fun saveDeviceToken(@Header("Authorization") authtoken: String,@Path("deviceId") deviceToken : String,@Path("token") token : String) : Call<SaveUserTokenResponse>

    @Headers("Accept: application/json")
    @GET("register/sendVerifyEmailOtp")
    fun sendVerifyEmailOtp(@Query("email") email : String) : Call<RegisterResponse>

    @Headers("Accept: application/json")
    @POST("register/verifyEmailOtp")
    fun verifyEmailOtp(@Header("Content-Type") headerValue:String,
                        @Body otpVerify : OtpVerifyModel
    ) : Call<RegisterResponse>

    @Headers("Accept: application/json")
    @GET("register/getAllCountries")
    fun getAllCountries() : Call<CountryResponse>

    @Headers("Accept: application/json")
    @POST("register/user")
    fun registerUserPhoto(@Header("Content-Type") headerValue:String,
                          @Query("registerRequest") registerRequest : String,
                          @Body brandLogo : MultipartBody): Call<RegisterResponse>

    @Headers("Accept: application/json")
    @POST("register/user/")
    fun registerUser(@Query("registerRequest", encoded = false) registerRequest : String): Call<RegisterResponse>

//    @Headers("Accept: application/json")
//    @POST("register/user")
//    fun registerArtisanPhoto(@Header("Content-Type") headerValue:String,
//                           @Query("registerRequest") registerRequest : String,
//                           @Body brandLogo : MultipartBody): Call<RegisterResponse>
//
//    @Headers("Accept: application/json")
//    @POST("register/user/")
//    fun registerArtisan(@Query("registerRequest", encoded = false) registerRequest : String): Call<RegisterResponse>


    @Headers("Accept: application/json")
    @POST("register/verifyWeaverDetails")
    fun verifyArtisanDetails(@Header("Content-Type") headerValue:String,
                             @Body weaverDetails : ArtisanidModel
    ) : Call<RegisterResponse>



}