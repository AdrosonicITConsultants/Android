package com.adrosonic.craftexchange.repository.remote

import com.adrosonic.craftexchange.repository.data.response.cms.CMSDataResponse
import com.adrosonic.craftexchange.repository.data.response.cms.CMSDataResponseElement
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
    @GET("pages/64")//prod
    // @GET("pages/27")//qa uat
    fun getPagesData() : Call<CMSDataResponseElement>

    @Headers("Accept: application/json")
    @GET("categoriesselfdesign")
    fun categoriesselfdesign() : Call<CMSDataResponse>

    @Headers("Accept: application/json")
    @GET("categoriescodesign")
    fun categoriescodesign() : Call<CMSDataResponse>
}