package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.loginResponse.LoginValidationResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchange.repository.data.response.admin.login.AdminResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.login.BuyerResponse
import com.adrosonic.craftexchange.repository.data.model.UserAuthModel
import com.adrosonic.craftexchange.repository.data.request.authModel.AdminAuthModel

import retrofit2.Call
import retrofit2.http.*

interface LoginDao {

    @Headers("Accept: application/json")
    @POST("login/authenticate")
    fun authenticateBuyer(@Header("Content-Type") headerValue:String,
                          @Body userAuthenticate : UserAuthModel
    ) : Call<BuyerResponse>

    @Headers("Accept: application/json")
    @POST("login/authenticate")
    fun authenticateArtisan(@Header("Content-Type") headerValue:String,
                          @Body userAuthenticate : UserAuthModel
    ) : Call<ArtisanResponse>

    @Headers("Accept: application/json")
    @GET("login/validateusername")
    fun validateUserName(@Query("emailOrMobile") emailOrMobile : String,
    @Query("roleId") roleId : Long) : Call<LoginValidationResponse>

    @Headers("Accept: application/json")
    @POST("/login/authenticateMarketing")
    fun authenticateAdmin(@Header("Content-Type") headerValue:String,
                          @Body userAuthenticate : AdminAuthModel
    ) : Call<AdminResponse>

}