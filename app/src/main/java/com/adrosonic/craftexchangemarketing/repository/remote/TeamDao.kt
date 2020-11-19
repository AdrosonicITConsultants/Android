package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.team.AdminsRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminRolesResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminsResponse
import retrofit2.Call
import retrofit2.http.*

interface TeamDao {

    @Headers("Accept: application/json")
    @POST("/marketingTeam/getAdmins")
    fun getAdmins(@Header("Authorization") token:String,
                  @Body adminRequest : AdminsRequest) : Call<AdminsResponse>

    @Headers("Accept: application/json")
    @GET("marketingTeam/getAdmin")
    fun getAdmin(@Header("Authorization") token:String,
                 @Query("id") id : Int) : Call<AdminResponse>

    @Headers("Accept: application/json")
    @GET("marketingTeam/getAdminRoles")
    fun getAdminRoles(@Header("Authorization") token:String) : Call<AdminRolesResponse>

}