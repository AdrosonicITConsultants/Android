package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.model.UserAuthModel
import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.UserDataRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.login.BuyerResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SuggestionResponse
import retrofit2.Call
import retrofit2.http.*

interface UserDatabaseDao {

    @Headers("Accept: application/json")
    @POST("api/marketingTeam/getUsers")
    fun getUserData(@Header("Authorization") token:String,
                              @Body userDataRequest : UserDataRequest) : Call<DatabaseResponse>

    @Headers("Accept: application/json")
    @POST("api/marketingTeam/getUserCount")
    fun getUserDataCount(@Header("Authorization") token:String,
                    @Body userDataRequest : UserDataRequest) : Call<DatabaseCountResponse>

}