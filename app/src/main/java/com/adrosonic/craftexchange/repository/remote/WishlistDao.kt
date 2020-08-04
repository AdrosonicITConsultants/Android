package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchange.repository.data.response.buyer.wishList.WishListedIds
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface WishlistDao {

    @Headers("Accept: application/json")
    @POST("product/addToWishlist/{productId}")
    fun addToWishlist(@Header("Authorization") token:String,
                      @Path("productId") productId : Long
    ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @DELETE("product/deleteProductsInWishlist/{productId}")
    fun deleteProductsInWishlist(@Header("Authorization") token:String,
                                 @Path("productId") productId : Long
    ) : Call<ResponseBody>

    @Headers("Accept: application/json")
    @DELETE("product/deleteAllProductsInWishlist")
    fun deleteAllProductsInWishlist(@Header("Authorization") token:String
    ) : Call<ResponseBody>

    //////////////////////////////////////WishlistedProducts////////////////////////////////////////
    @Headers("Accept: application/json")
    @GET("product/getProductIdsInWishlist")
    fun getWishlistedProductIds(@Header("Authorization") token: String): Call<WishListedIds>

    @Headers("Accept: application/json")
    @GET("product/getProduct/{productId}")
    fun getSingleProductDetails(@Header("Authorization") token: String,@Path("productId")productId:Int): Call<SingleProductDetails>

}