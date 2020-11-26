package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.CalogueRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.*
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryProductResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SearchProdData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ProductCatalogueDao {

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/getArtisanProducts/{clusterId}/{availability}/{statusId}")
    fun getArtisanProducts(@Header("Authorization") token:String,
                           @Query ("clusterId")clusterId:Int?,
                           @Query ("availability")availability:Int?,
                           @Query ("statusId")statusId:Int?
    ) : Call<ProductCatalogueResponse>

    @Headers("Accept: application/json")
    @POST("api/marketingTeam/v1/getArtisanProductsCount")
    fun getArtisanProductsCount(@Header("Authorization") token:String,
                           @Body catalogueRequest : CalogueRequest
    ) : Call<ProductCountResponse>

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/getArtisanProduct/{productId}")
    fun getArtisanProduct(
        @Header("Authorization") token: String,
        @Path("productId") productId : Long
    ): Call<ProductDetailsResponse>

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/getCustomProduct/{productId}")
    fun getCustomProduct(
        @Header("Authorization") token: String,
        @Path("productId") productId : Long
    ): Call<ProductDetailsResponse>

    @Headers("Accept: application/json")
    @GET("api/marketingTeam/getFilteredArtisans")
    fun getFilteredArtisans(
        @Header("Authorization") token: String,
        @Query("clusterId") clusterId: Int,
        @Query("searchString") searchString: String
    ): Call<FilteredArtisanResponse>

    @Headers("Accept: application/json")
    @POST("api/marketingTeam/uploadProduct")
    fun uploadProductTemplate(
        @Header("Authorization") token: String,
        @Header("Content-Type") headerValue:String,
        @Header("Content-Length") length: Long,
        @Query("productData") productData: String,
        @Query("artisanId") artisanId: Int,
        @Body file: MultipartBody
    ): Call<ArtisanProductTemplateRespons>

    @Headers("Accept: application/json")
    @PUT("api/product/edit/product")
    fun editProductTemplate(
        @Header("Authorization") token: String,
        @Header("Content-Type") headerValue:String,
        @Header("Content-Length") length: Long,
        @Query("productData") productData: String,
        @Body file: MultipartBody
    ): Call<EditProductCatResponse>

    @Headers("Accept: application/json")
    @DELETE("api/product/deleteProduct/{productId}")
    fun deleteProducts(
        @Header("Authorization") token: String,
        @Path("productId") productId: Int
    ): Call<DeleteOwnProductRespons>
}