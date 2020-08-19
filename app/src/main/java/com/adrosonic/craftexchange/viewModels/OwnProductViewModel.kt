package com.adrosonic.craftexchange.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.GetAllOwnDesignResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import javax.security.auth.callback.Callback

class OwnProductViewModel : ViewModel() {
    interface OwnProductFetchedInterface{
        fun onSuccess()
        fun onFailure()
    }

    var listener: OwnProductViewModel.OwnProductFetchedInterface? = null
    val customDesignListData : MutableLiveData<RealmResults<BuyerCustomProduct>> by lazy { MutableLiveData<RealmResults<BuyerCustomProduct>>() }

    fun getCustomDesignListMutableData(): MutableLiveData<RealmResults<BuyerCustomProduct>> {
        customDesignListData.value=loadCustomDesignListData()
        return customDesignListData
    }
    fun loadCustomDesignListData(): RealmResults<BuyerCustomProduct> {
        var customProducts= BuyerCustomProductPredicates.getCustomProductData()
//        Log.e("CustomDesign","loadwishListData :"+customProducts?.size)
        return customProducts!!
    }

    fun getCustomProducts(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getBuyerOenDesignService()
            .getAllOwnDesignProducts(token)
            .enqueue(object: Callback, retrofit2.Callback<GetAllOwnDesignResponse> {
                override fun onFailure(call: Call<GetAllOwnDesignResponse>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailure()
                    Log.e("OwnProduct","onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<GetAllOwnDesignResponse>,
                    response: retrofit2.Response<GetAllOwnDesignResponse>) {

                    if(response.body()?.valid == true){
                        Log.e("OwnProduct","ownProduct size :"+response.body()?.data?.size)
                        BuyerCustomProductPredicates.deleteAllOwnProducts()
                        response.body()?.data?.let {
                            BuyerCustomProductPredicates.insertCustomProductsProduct(response.body()?.data!!)
                        }
                        listener?.onSuccess()
                    }else{
                        listener?.onFailure()
                        Log.e("OwnProduct","onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }
}