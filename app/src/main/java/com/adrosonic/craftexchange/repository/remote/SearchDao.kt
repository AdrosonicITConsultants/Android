package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.request.search.SearchProduct
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.search.SearchProductResponse
import com.adrosonic.craftexchange.repository.data.response.search.SuggestionResponse
import retrofit2.Call
import retrofit2.http.*

interface SearchDao {

    @Headers("Accept: application/json")
    @GET("search/getArtisanSuggestions")
    fun getArtisanSuggestions(@Header("Authorization") token:String,
                              @Query("str") str : String) : Call<SuggestionResponse>

    @Headers("Accept: application/json")
    @GET("search/getSuggestions")
    fun getBuyerSuggestions(@Header("Authorization") token:String,
                            @Query("str") str : String) : Call<SuggestionResponse>


    @Headers("Accept: application/json")
    @POST("search/searchArtisanProducts")
    fun searchArtisanProducts(@Header("Authorization") token:String,
                       @Body searchProduct : SearchProduct) : Call<SearchProductResponse>

    @Headers("Accept: application/json")
    @POST("search/searchProducts")
    fun searchProducts(@Header("Authorization") token:String,
                        @Body searchProduct : SearchProduct) : Call<SearchProductResponse>

    @Headers("Accept: application/json")
    @POST("search/searchProductCount")
    fun searchProductCount(@Header("Authorization") token:String,
                       @Body searchProduct : SearchProduct) : Call<SearchProductResponse>
}