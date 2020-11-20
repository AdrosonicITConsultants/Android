package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.search.SearchProduct
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SearchProductResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SuggestionResponse
import retrofit2.Call
import retrofit2.http.*

interface SearchDao {

    @Headers("Accept: application/json")
    @GET("api/search/getArtisanSuggestions")
       fun getArtisanSuggestions(@Header("Authorization") token:String,
                              @Query("str") str : String) : Call<SuggestionResponse>

    @Headers("Accept: application/json")
    @GET("api/search/getSuggestions")
    fun getSuggestions(@Header("Authorization") token:String,
                              @Query("str") str : String) : Call<SuggestionResponse>


    @Headers("Accept: application/json")
    @POST("api/search/searchArtisanProducts")
    fun searchArtisanProducts(@Header("Authorization") token:String,
                       @Body searchProduct : SearchProduct) : Call<SearchProductResponse>

    @Headers("Accept: application/json")
    @POST("api/search/searchProducts")
    fun searchProducts(@Header("Authorization") token:String,
                        @Body searchProduct : SearchProduct) : Call<SearchProductResponse>
}