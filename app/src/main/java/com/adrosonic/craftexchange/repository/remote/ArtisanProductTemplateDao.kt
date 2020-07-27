package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import retrofit2.Call
import retrofit2.http.*

interface ArtisanProductTemplateDao {

    @Headers("Accept: application/json")
    @POST("product/uploadproduct")
    fun uploadProductTemplate(@Body file1:Multipart,
                              @Body file2:Multipart,
                              @Body file3:Multipart,
                              @Query("productData")productData:String) : Call<ArtisanProductTemplateRespons>
}