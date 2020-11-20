package com.adrosonic.craftexchangemarketing.repository.remote

import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.CalogueRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductCatalogueResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryProductResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SearchProdData
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
}