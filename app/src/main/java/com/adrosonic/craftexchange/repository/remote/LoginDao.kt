package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.loginResponse.LoginAuthResponse
import com.adrosonic.craftexchange.repository.data.loginResponse.LoginValidationResponse
import com.adrosonic.craftexchange.repository.data.model.UserAuthModel
import retrofit2.Call
import retrofit2.http.*

interface LoginDao {

    @Headers("Accept: application/json")
    @POST("login/authenticate")
    fun authenticate(@Header("Content-Type") headerValue:String,
                     @Body userAuthenticate : UserAuthModel
    ) : Call<LoginAuthResponse>

    @Headers("Accept: application/json")
    @GET("login/validateusername")
    fun validateUserName(@Query("emailOrMobile") emailOrMobile : String,
    @Query("roleId") roleId : Long) : Call<LoginValidationResponse>

}