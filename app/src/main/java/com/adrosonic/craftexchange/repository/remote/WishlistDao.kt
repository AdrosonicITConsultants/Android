package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
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
}