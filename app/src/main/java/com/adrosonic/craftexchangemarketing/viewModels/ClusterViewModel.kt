package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.NotificationPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.clusterResponse.CLusterResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
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
    interface notificationInterface{
        fun onNotificationDataFetched()
    }
    var noficationlistener: notificationInterface? = null
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

    fun getClusterProdListMutableData(clusterID : Long): MutableLiveData<RealmResults<ProductCatalogue>> {
        clusterProdList.value=loadClusterProdList(clusterID)
        return clusterProdList
    }

    fun loadClusterProdList(clusterID : Long): RealmResults<ProductCatalogue> {
        var clusterProdList = ProductPredicates.getClusterProductsFromId(clusterID)
        Log.e("clusterProdList","clusterProdList :"+clusterProdList?.size)
        return clusterProdList!!
    }


    fun getAllClusters(){
        craftexchangemarketingRepository
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

        craftexchangemarketingRepository
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
                        ProductPredicates.insertProductsInCatalogue(response.body()?.data?.products)
                        clusterListener?.onSuccess()
                    } else {
                        clusterListener?.onFailure()
                    }
                }
            })

    }

    fun getAllNotifications(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getNotificationService()
            .getAllAdminNotifications(token)
            .enqueue(object: Callback, retrofit2.Callback<NotificationResponse> {
                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e(LandingViewModel.TAG,"onFailure: "+t.message)
                    noficationlistener?.onNotificationDataFetched()
                }
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>) {
                    if(response.body()?.valid == true){
                        Log.e(LandingViewModel.TAG,"getProductUploadData :"+response.body()?.data?.getAllNotifications?.size)
                        NotificationPredicates.insertNotification(response.body()?.data?.getAllNotifications)
                        UserConfig.shared?.notiBadgeCount=response.body()?.data?.count?:0
                        noficationlistener?.onNotificationDataFetched()
                    }else{
                        Log.e(NotificationViewModel.TAG,"getProductUploadData onFailure: "+response.body()?.errorCode)
                        noficationlistener?.onNotificationDataFetched()
                    }
                }
            })
    }
}