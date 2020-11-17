package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ClusterPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.CLusterResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ClusterViewModel(application: Application) : AndroidViewModel(application) {

    interface ClusterProdInterface{
        fun onFailure()
        fun onSuccess()
    }


    var clusterListener : ClusterProdInterface ?= null

    val clusterList : MutableLiveData<RealmResults<ClusterList>> by lazy { MutableLiveData<RealmResults<ClusterList>>() }
    val clusterProdList : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }

    fun getClusterListMutableData(): MutableLiveData<RealmResults<ClusterList>> {
        clusterList.value=loadClusterList()
        return clusterList
    }

    fun loadClusterList(): RealmResults<ClusterList> {
        var clusterList = ClusterPredicates.getAllClusters()
        Log.e("clusterList","clusterList :"+clusterList?.size)
        return clusterList!!
    }

    fun getClusterProdListMutableData(clusterID : Long,madWithAnt : Long): MutableLiveData<RealmResults<ProductCatalogue>> {
        clusterProdList.value=loadClusterProdList(clusterID,madWithAnt)
        return clusterProdList
    }

    fun loadClusterProdList(clusterID : Long,madWithAnt:Long): RealmResults<ProductCatalogue> {
        var clusterProdList = ProductPredicates.getClusterProductsFromId(clusterID,madWithAnt)
        Log.e("clusterProdList","clusterProdList :"+clusterProdList?.size)
        return clusterProdList!!
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

    fun getProductsByCluster(clusterID : Long) {
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN, "")}"

        CraftExchangeRepository
            .getProductService()
            .getProductByCluster(token, clusterID)
            .enqueue(object : Callback, retrofit2.Callback<CatalogueProductsResponse> {
                override fun onFailure(call: Call<CatalogueProductsResponse>, t: Throwable) {
                    t.printStackTrace()
                    clusterListener?.onFailure()
                }

                override fun onResponse(
                    call: Call<CatalogueProductsResponse>,
                    response: Response<CatalogueProductsResponse>
                ) {
                    if (response.body()?.valid == true) {
                        ProductPredicates.insertProductsInCatalogue(response.body()?.data?.products,0)
                        clusterListener?.onSuccess()
                    } else {
                        clusterListener?.onFailure()
                    }
                }
            })

    }
}