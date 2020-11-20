package com.adrosonic.craftexchangemarketing.repository.remote

//import com.adrosonic.craftexchangemarketing.repository.data.response.clusterResponse.CLusterResponse
//import com.adrosonic.craftexchangemarketing.repository.data.response.clusterResponse.ProductResponse

import com.adrosonic.craftexchangemarketing.repository.data.response.clusterResponse.CLusterResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ClusterDao {

    @Headers("Accept: application/json")
    @GET("api/cluster/getAllClusters")
    fun getAllClusters() : Call<CLusterResponse>

//    @Headers("Accept: application/json")
//    @GET("cluster/getProductCategories/{clusterId}")
//    fun getProductCategories(@Path("clusterId")clusterId:Int) : Call<ProductResponse>

}