package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.wishList.WishListedIds
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ProductDao {

    @Headers("Accept: application/json")
    @GET("product/getAllProducts")
    fun getAllProducts(): Call<AllProductsResponse>

    @Headers("Accept: application/json")
    @GET("filter/getFilteredArtisans")
    fun getFilteredArtisans(@Header("Authorization") token: String): Call<BrandListResponse>

    ////////////////////////////////Catalogue APIs//////////////////////////////////////
    @Headers("Accept: application/json")
    @GET("product/getProductByArtisan/{artisanId}")
    fun getProductsByArtisan(
        @Header("Authorization") token: String,
        @Path("artisanId") artisanId: Long
    ): Call<CatalogueProductsResponse>

    @Headers("Accept: application/json")
    @GET("product/getClusterProducts/{clusterId}")
    fun getProductByCluster(
        @Header("Authorization") token: String,
        @Path("clusterId") clusterId: Long
    ): Call<CatalogueProductsResponse>

    @Headers("Accept: application/json")
    @GET("product/getProductCategoryProducts/{productCategoryId}")
    fun getProductByCategory(
        @Header("Authorization") token: String,
        @Path("productCategoryId") productCategoryId: Long
    ): Call<CatalogueProductsResponse>

    ////////////////////////////////////////////////////////////////////////////////////
    //Artisan Landing Screen
    @Headers("Accept: application/json")
    @GET("product/getArtitionProducts")
    fun getArtisanProducts(@Header("Authorization") token: String): Call<ArtisanProductDetailsResponse>

    //////////////////////////////////////////add product template artisan///////////////////////////////////////////////////
    @Headers("Accept: application/json")
    @GET("product/getProductUploadData")
    fun getProductUploadData(@Header("Authorization") token: String): Call<ProductUploadData>

    @Headers("Accept: application/json")
    @POST("product/uploadProduct")
    fun uploadProductTemplate(
        @Header("Authorization") token: String,
        @Header("Content-Type") headerValue:String,
        @Header("Content-Length") length: Long,
        @Query("productData") productData: String,
        @Body file: MultipartBody
    ): Call<ArtisanProductTemplateRespons>

  }