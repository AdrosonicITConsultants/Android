package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.AddOwnDesignResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.GetAllOwnDesignResponse
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface BuyerOwnDesignDao {

    @Headers("Accept: application/json")
    @POST("/buyerCustomProduct/uploadProduct")
    fun uploadOwnProduct(
        @Header("Authorization") token: String,
        @Header("Content-Type") headerValue: String,
        @Header("Content-Length") length: Long,
        @Query("productData") productData: String,
        @Body file: MultipartBody
    ): Call<AddOwnDesignResponse>

    @Headers("Accept: application/json")
    @PUT("/buyerCustomProduct/edit/product")
    fun updateOwnProduct(
        @Header("Authorization") token: String,
        @Header("Content-Type") headerValue:String,
        @Header("Content-Length") length: Long,
        @Query("productData") productData: String,
        @Body file: MultipartBody
    ): Call<AddOwnDesignResponse>

    @Headers("Accept: application/json")
    @GET("/buyerCustomProduct/getAllProducts")
    fun getAllOwnDesignProducts(
        @Header("Authorization") token: String
    ): Call<GetAllOwnDesignResponse>

    @Headers("Accept: application/json")
    @DELETE("/buyerCustomProduct/deleteProduct/{productId}")
    fun deleteOwnDesignProducts(
        @Header("Authorization") token: String,
        @Path("productId") productId: Int
    ): Call<DeleteOwnProductRespons>
}