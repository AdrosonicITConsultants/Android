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
    @GET("api/marketingTeam/userProfile/{userId}")
    fun getUserData(@Header("Authorization") token:String,
                    @Path("userId") userId : Long
    ) : Call<UserProfileResponse>

    @Headers("Accept: application/json")
    @POST("api/marketingTeam/activateUser/{userId}")
    fun activateUser(@Header("Authorization") token:String,
                    @Path("userId") userId : Long
    ) : Call<UserStatusResponse>

    @Headers("Accept: application/json")
    @POST("api/marketingTeam/deactivateUser/{userId}")
    fun deactivateUser(@Header("Authorization") token:String,
                     @Path("userId") userId : Long
    ) : Call<UserStatusResponse>

    @Headers("Accept: application/json")
    @POST("api/marketingTeam/editRating/{userId}/{rating}")
    fun setRating(@Header("Authorization") token:String,
                       @Path("userId") userId : Long,
                  @Path("rating") rating : Float?
    ) : Call<UserStatusResponse>
}