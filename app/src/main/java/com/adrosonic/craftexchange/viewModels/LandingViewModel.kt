package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchange.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import javax.security.auth.callback.Callback

class LandingViewModel(application: Application) : AndroidViewModel(application) {


    interface wishlistFetchedInterface{
        fun onSuccess()
        fun onFailure()
    }
    var listener: wishlistFetchedInterface? = null
    val wishListData : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }
    fun getwishListMutableData(): MutableLiveData<RealmResults<ProductCatalogue>> {
        wishListData.value=loadwishListData()
        return wishListData
    }
    fun loadwishListData(): RealmResults<ProductCatalogue> {
        var wishList=ProductPredicates.getWishListedData()
        Log.e("Wishlist","loadwishListData :"+wishList?.size)
        return wishList!!
    }

    fun logoutUser() {
        val editor = Prefs.edit()
        editor.clear()
        editor.commit()
        editor.apply()
    }

    fun getProductsOfArtisan(context : Context){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getArtisanProducts(token)
            .enqueue(object: Callback, retrofit2.Callback<ArtisanProductDetailsResponse> {
                override fun onFailure(call: Call<ArtisanProductDetailsResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<ArtisanProductDetailsResponse>,
                    response: retrofit2.Response<ArtisanProductDetailsResponse>) {
                    if(response.body()?.valid == true){
                        ProductPredicates.insertProductsOfArtisan(response.body())
                    }else{
                        Utility.displayMessage(response.body()?.errorMessage.toString(),context)
                    }
                }

            })
    }

    fun getProductUploadData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getProductUploadData(token)
            .enqueue(object: Callback, retrofit2.Callback<ProductUploadData> {
                override fun onFailure(call: Call<ProductUploadData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getProductUploadData onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<ProductUploadData>,
                    response: retrofit2.Response<ProductUploadData>) {

                    if(response.body()?.valid == true){
                        //todo store data to spf
                        Log.e("LandingViewModel","getProductUploadData :"+response.body()?.data?.productCare?.size)
                        UserConfig.shared.productUploadJson= Gson().toJson(response.body())
                        Log.e("LandingViewModel","SPF :"+UserConfig.shared.productUploadJson)

                    }else{
                        Log.e("LandingViewModel","getProductUploadData onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getwishlisteProductIds(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getWishlistedProductIds(token)
            .enqueue(object: Callback, retrofit2.Callback<WishListedIds> {
                override fun onFailure(call: Call<WishListedIds>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailure()
                    Log.e("LandingViewModel","wishlist onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<WishListedIds>,
                    response: retrofit2.Response<WishListedIds>) {

                    if(response.body()?.valid == true){
                        Log.e("LandingViewModel","wishlist :"+response.body()?.data?.joinToString())
                        listener?.onSuccess()
                        ProductPredicates.addToWishlist(response.body()?.data)
                    }else{
                        listener?.onFailure()
                        Log.e("LandingViewModel","wishlist onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

}