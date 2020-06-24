package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.model.artisan.ArtisanidModel
import com.adrosonic.craftexchange.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.repository.data.model.buyer.User
import com.adrosonic.craftexchange.repository.data.registerResponse.CountryResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface RegisterDao {

    @Headers("Accept: application/json")
    @GET("register/sendVerifyEmailOtp")
    fun sendVerifyEmailOtp(@Query("email") email : String) : Call<RegisterResponse>

    @Headers("Accept: application/json")
    @POST("register/verifyEmailOtp")
    fun verifyEmailOtp(@Header("Content-Type") headerValue:String,
                        @Body otpVerify : OtpVerifyModel
    ) : Call<RegisterResponse>

    //TODO : Implement for image upload during registration
//    @Multipart
//    @POST("register/user")
//    fun registerBuyer(@Part("registerRequest") registerRequest : String,
//                      @Header("Content-Type") headerValue:String,
//                      @Part profilePic : MultipartBody.Part
//    ): Call<RegisterResponse>


    @Headers("Accept: application/json")
    @POST("register/user/")
    fun registerBuyer(@Query("registerRequest", encoded = false) registerRequest : String): Call<RegisterResponse>

    @Headers("Accept: application/json")
    @GET("register/getAllCountries")
    fun getAllCountries() : Call<CountryResponse>

    @Headers("Accept: application/json")
    @POST("register/verifyWeaverDetails")
    fun verifyArtisanDetails(@Header("Content-Type") headerValue:String,
                             @Body weaverDetails : ArtisanidModel
    ) : Call<RegisterResponse>

    //TODO : Implement for image upload during registration
//    @Headers("Accept: application/json")
//    @POST("register/user")
//    fun registerArtisan(@Header("Content-Type") headerValue:String,
//                      @Body registerRequest : com.adrosonic.craftexchange.repository.data.model.artisan.User
//    ): Call<RegisterResponse>

    @Headers("Accept: application/json")
    @POST("register/user/")
    fun registerArtisan(@Query("registerRequest", encoded = false) registerRequest : String): Call<RegisterResponse>

}