package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserStatusResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.editProfile.EditDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.profile.ProfileResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface IndividualUserDao {

    @Headers("Accept: application/json")
    @GET("/marketingTeam/userProfile/{userId}")
    fun getUserData(@Header("Authorization") token:String,
                    @Path("userId") userId : Int
    ) : Call<UserProfileResponse>

    @Headers("Accept: application/json")
    @POST("/marketingTeam/activateUser/{userId}")
    fun activateUser(@Header("Authorization") token:String,
                    @Path("userId") userId : Int
    ) : Call<UserStatusResponse>

    @Headers("Accept: application/json")
    @POST("/marketingTeam/deactivateUser/{userId}")
    fun deactivateUser(@Header("Authorization") token:String,
                     @Path("userId") userId : Int
    ) : Call<UserStatusResponse>
}