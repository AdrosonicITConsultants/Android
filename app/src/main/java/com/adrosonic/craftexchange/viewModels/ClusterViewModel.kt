package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.database.predicates.ClusterPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.CLusterResponse
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ClusterViewModel(application: Application) : AndroidViewModel(application) {

    interface ClusterListInterface{
        fun onFailure()
        fun onSuccess()
    }

    var clusterListener : ClusterListInterface ?= null

    val clusterList : MutableLiveData<RealmResults<ClusterList>> by lazy { MutableLiveData<RealmResults<ClusterList>>() }

    fun getClusterListMutableData(): MutableLiveData<RealmResults<ClusterList>> {
        clusterList.value=loadClusterList()
        return clusterList
    }

    fun loadClusterList(): RealmResults<ClusterList> {
        var clusterList = ClusterPredicates.getAllClusters()
        Log.e("clusterList","clusterList :"+clusterList?.size)
        return clusterList!!
    }


    fun getAllClusters(){
        CraftExchangeRepository
            .getClusterService()
            .getAllClusters().enqueue(object : Callback, retrofit2.Callback<CLusterResponse> {
                override fun onFailure(call: Call<CLusterResponse>, t: Throwable) {
                    t.printStackTrace()
                    clusterListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<CLusterResponse>,
                    response: Response<CLusterResponse>
                ) {
                    if (response.body()?.valid == true) {
                        ClusterPredicates.insertClusters(response.body())
                        clusterListener?.onSuccess()

                    }else{
                        clusterListener?.onFailure()
                    }
                }
            })
    }

}