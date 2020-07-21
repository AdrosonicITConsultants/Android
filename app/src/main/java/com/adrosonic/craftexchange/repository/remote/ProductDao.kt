package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.viewProducts.brand.BrandProductDetailResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ProductDao {

    @Headers("Accept: application/json")
    @GET("product/getAllProducts")
    fun getAllProducts() : Call<AllProductsResponse>

    @Headers("Accept: application/json")
    @GET("filter/getFilteredArtisans")
    fun getFilteredArtisans(@Header("Authorization") token:String) : Call<BrandListResponse>

    @Headers("Accept: application/json")
    @GET("filter/getFilteredArtisans")
    fun getProductByArtisan(@Header("Authorization") token:String,
                            @Query("artisanId", encoded = false) artisanId : Int) : Call<BrandProductDetailResponse>
}