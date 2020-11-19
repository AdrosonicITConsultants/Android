package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.CalogueRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductCatalogueResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SearchProdData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ProductCatalogueDao {

    @Headers("Accept: application/json")
    @POST("/marketingTeam/v1/getArtisanProducts")
    fun getArtisanProducts(@Header("Authorization") token:String,
                      @Body catalogueRequest : CalogueRequest
    ) : Call<ProductCatalogueResponse>

    @Headers("Accept: application/json")
    @POST("/marketingTeam/v1/getArtisanProductsCount")
    fun getArtisanProductsCount(@Header("Authorization") token:String,
                           @Body catalogueRequest : CalogueRequest
    ) : Call<ProductCountResponse>


     @Headers("Accept: application/json")
    @DELETE("product/deleteAllProductsInWishlist")
    fun deleteAllProductsInWishlist(@Header("Authorization") token:String
    ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @GET("product/getProductIdsInWishlist")
    fun getWishlistedProductIds(@Header("Authorization") token: String): Call<WishListedIds>

 }