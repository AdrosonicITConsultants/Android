package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.brand.BrandProductDetailResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.cluster.ClusterProductDetailResponse
import retrofit2.Call
import retrofit2.http.*

interface ProductDao {

    @Headers("Accept: application/json")
    @GET("product/getAllProducts")
    fun getAllProducts() : Call<AllProductsResponse>

    @Headers("Accept: application/json")
    @GET("filter/getFilteredArtisans")
    fun getFilteredArtisans(@Header("Authorization") token:String) : Call<BrandListResponse>

    @Headers("Accept: application/json")
    @GET("product/getProductByArtisan/{artisanId}")
    fun getProductsByArtisan(@Header("Authorization") token:String,
                             @Path("artisanId") artisanId : Long) : Call<BrandProductDetailResponse>


    @Headers("Accept: application/json")
    @GET("product/getClusterProducts/{clusterId}")
    fun getProductByCluster(@Header("Authorization") token:String,
                            @Path("clusterId") clusterId : Long) : Call<ClusterProductDetailResponse>


    //Artisan Landing Screen
    @Headers("Accept: application/json")
    @GET("product/getArtitionProducts")
    fun getArtisanProducts(@Header("Authorization") token:String) : Call<ArtisanProductDetailsResponse>
}