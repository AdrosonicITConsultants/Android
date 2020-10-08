package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.logout.LogoutResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ArtisanProductsViewModel(application: Application) : AndroidViewModel(application)  {
    interface productsFetchInterface{
        fun onSuccess()
        fun onFailure()
    }
    var listener : productsFetchInterface ?= null
    val productListData : MutableLiveData<RealmResults<ArtisanProducts>> by lazy { MutableLiveData<RealmResults<ArtisanProducts>>() }

    fun logoutUser() {
        val editor = Prefs.edit()
        editor.clear()
        editor.commit()
        editor.apply()
        calllogoutUser()
    }
    fun calllogoutUser(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getUserService()
            .logoutUser(token, UserConfig.shared?.deviceRegistrationToken).enqueue(object : Callback, retrofit2.Callback<LogoutResponse> {
                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<LogoutResponse>,
                    response: Response<LogoutResponse>
                ) {

                }
            })
    }

    fun getProductListMutableData(artisanId : Long?,prodCat :String?): MutableLiveData<RealmResults<ArtisanProducts>> {
        productListData.value=loadProductListData(artisanId,prodCat)
        return productListData
    }

    fun loadProductListData(artisanId : Long?,prodCat :String?): RealmResults<ArtisanProducts> {
        var productList= ProductPredicates.getFilteredUploadedProducts(artisanId,prodCat)
        Log.e("Wishlist","loadwishListData :"+productList?.size)
        return productList!!
    }

    fun getProductCategoryListMutableData(artisanId : Long?): MutableLiveData<RealmResults<ArtisanProducts>> {
        productListData.value=loadProductCategoryList(artisanId)
        return productListData
    }

    fun loadProductCategoryList(artisanId : Long?): RealmResults<ArtisanProducts> {
        var productList= ProductPredicates.getProductCategoriesOfArtisan(artisanId)
        Log.e("ProductCategoryList","ProductCategoryListData :"+productList?.size)
        return productList!!
    }

    fun getProductsOfArtisan(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductService()
            .getArtisanProducts(token)
            .enqueue(object: Callback, retrofit2.Callback<ArtisanProductDetailsResponse> {
                override fun onFailure(call: Call<ArtisanProductDetailsResponse>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailure()
                }
                override fun onResponse(
                    call: Call<ArtisanProductDetailsResponse>,
                    response: retrofit2.Response<ArtisanProductDetailsResponse>) {
                    if(response.body()?.valid == true){
                        ProductPredicates.insertProductsOfArtisan(response.body())
                        listener?.onSuccess()
                    }else{
                        listener?.onFailure()
                    }
                }

            })
    }
}