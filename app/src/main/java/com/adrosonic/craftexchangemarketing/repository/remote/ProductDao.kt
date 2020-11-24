package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryProductResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ProductDao {

    @Headers("Accept: application/json")
    @GET("api/product/getAllProducts")
    fun getAllProducts(): Call<AllProductsResponse>

    @Headers("Accept: application/json")
    @GET("api/product/getProduct/{productId}")
    fun getSingleProduct(
        @Header("Authorization") token: String,
        @Path("productId") productId : Long
    ): Call<EnquiryProductResponse>

    @Headers("Accept: application/json")
    @GET("api/filter/getFilteredArtisans")
    fun getFilteredArtisans(@Header("Authorization") token: String): Call<BrandListResponse>

    ////////////////////////////////Catalogue APIs//////////////////////////////////////
    @Headers("Accept: application/json")
    @GET("api/product/getProductByArtisan/{artisanId}")
    fun getProductsByArtisan(
        @Header("Authorization") token: String,
        @Path("artisanId") artisanId: Long
    ): Call<CatalogueProductsResponse>

    @Headers("Accept: application/json")
    @GET("api/product/getClusterProducts/{clusterId}")
    fun getProductByCluster(
        @Header("Authorization") token: String,
        @Path("clusterId") clusterId: Long
    ): Call<CatalogueProductsResponse>

    @Headers("Accept: application/json")
    @GET("api/product/getProductCategoryProducts/{productCategoryId}")
    fun getProductByCategory(
        @Header("Authorization") token: String,
        @Path("productCategoryId") productCategoryId: Long
    ): Call<CatalogueProductsResponse>

    ////////////////////////////////////////////////////////////////////////////////////
    //Artisan Landing Screen
    @Headers("Accept: application/json")
    @GET("api/product/getArtitionProducts")
    fun getArtisanProducts(@Header("Authorization") token: String): Call<ArtisanProductDetailsResponse>

    //////////////////////////////////////////product template artisan///////////////////////////////////////////////////
    @Headers("Accept: application/json")
    @GET("api/product/getProductUploadData")
    fun getProductUploadData(@Header("Authorization") token: String): Call<ProductUploadData>

    @Headers("Accept: application/json")
    @POST("api/product/uploadProduct")
    fun uploadProductTemplate(
        @Header("Authorization") token: String,
        @Header("Content-Type") headerValue:String,
        @Header("Content-Length") length: Long,
        @Query("productData") productData: String,
        @Body file: MultipartBody
    ): Call<ArtisanProductTemplateRespons>

    @Headers("Accept: application/json")
    @PUT("api/product/edit/product")
    fun updateProductTemplate(
        @Header("Authorization") token: String,
        @Header("Content-Type") headerValue:String,
        @Header("Content-Length") length: Long,
        @Query("productData") productData: String,
        @Body file: MultipartBody
    ): Call<ArtisanProductTemplateRespons>

    @Headers("Accept: application/json")
    @GET("api/Product/{productId}/{imagename}")
    fun getProductImage(
        @Path("productId") productId: Long,
        @Path("imagename") imagename: String
    ):Call<ResponseBody>

    @Headers("Accept: application/json")
    @DELETE("api/product/deleteProduct/{productId}")
    fun deleteProductsTemplate(
        @Header("Authorization") token: String,
        @Path("productId") productId: Int
    ): Call<DeleteOwnProductRespons>
  }