package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.viewProducts.brand.BrandProductDetailResponse
import com.adrosonic.craftexchange.repository.data.response.viewProducts.cluster.ClusterProductDetailResponse
import okhttp3.ResponseBody
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
    fun getProductByArtisan(@Header("Authorization") token:String,
                            @Path("artisanId") artisanId : Long) : Call<BrandProductDetailResponse>


    @Headers("Accept: application/json")
    @GET("product/getClusterProducts/{clusterId}")
    fun getProductByCluster(@Header("Authorization") token:String,
                            @Path("clusterId") clusterId : Long) : Call<ClusterProductDetailResponse>

}