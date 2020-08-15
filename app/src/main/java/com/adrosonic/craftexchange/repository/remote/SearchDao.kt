package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.search.SuggestionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface SearchDao {

    @Headers("Accept: application/json")
    @GET("search/getArtisanSuggestions")
    fun getArtisanSuggestions(@Header("Authorization") token:String,
                              @Query("str") str : String) : Call<SuggestionResponse>

    @Headers("Accept: application/json")
    @GET("search/getSuggestions")
    fun getSuggestions(@Header("Authorization") token:String,
                              @Query("str") str : String) : Call<SuggestionResponse>
}