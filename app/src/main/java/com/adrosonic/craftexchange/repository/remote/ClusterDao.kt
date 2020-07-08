package com.adrosonic.craftexchange.repository.remote

//import com.adrosonic.craftexchange.repository.data.clusterResponse.CLusterResponse
//import com.adrosonic.craftexchange.repository.data.clusterResponse.ProductResponse

import com.adrosonic.craftexchange.repository.data.clusterResponse.CLusterResponse
import com.adrosonic.craftexchange.repository.data.clusterResponse.ProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ClusterDao {

    @Headers("Accept: application/json")
    @GET("cluster/getAllClusters")
    fun getAllClusters() : Call<CLusterResponse>

    @Headers("Accept: application/json")
    @GET("cluster/getProductCategories/{clusterId}")
    fun getProductCategories(@Path("clusterId")clusterId:Int) : Call<ProductResponse>

}