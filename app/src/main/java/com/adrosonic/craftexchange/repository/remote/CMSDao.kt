package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.cms.CMSDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface CMSDao {
    @Headers("Accept: application/json")
    @GET("demo-video")
    fun getDemoVideo() : Call<CMSDataResponse>

    @Headers("Accept: application/json")
    @GET("regions")
    fun getRegionData() : Call<CMSDataResponse>

    @Headers("Accept: application/json")
    @GET("categories")
    fun getCategoriesData() : Call<CMSDataResponse>

    @Headers("Accept: application/json")
    @GET("pages")
    fun getPagesData() : Call<CMSDataResponse>

    @Headers("Accept: application/json")
    @GET("categoriesselfdesign")
    fun categoriesselfdesign() : Call<CMSDataResponse>

    @Headers("Accept: application/json")
    @GET("categoriescodesign")
    fun categoriescodesign() : Call<CMSDataResponse>
}