package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.services.wishlist.WishlistService
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.ResponseBody
import retrofit2.Call
import javax.security.auth.callback.Callback

class WishlistViewModel(application: Application) : AndroidViewModel(application) {

    interface WishListInterface{
        fun onSuccess()
        fun onFailure()
    }
    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
    var listener: WishListInterface? = null

    fun addProductToWishlist(productId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("Wishlist","productId :"+productId)
        CraftExchangeRepository
            .getWishlistService()
            .addToWishlist(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Wishlist","Failuren  ${t.printStackTrace()}")
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: retrofit2.Response<NotificationReadResponse>) {
                    Log.e("Wishlist","onResponse :"+response.code())
                    Log.e("Wishlist","onResponse :"+response.isSuccessful)
                    Log.e("Wishlist","onResponse :"+call.request().url)
                    if(response.isSuccessful){
                        Log.e("Wishlist","addToWishlist :"+response.body())
                        WishlistPredicates.updateProductWishlisting(productId,1L,0L)
                    }else{
                        Log.e("Wishlist","addToWishlist "+response.body())
                    }
                }

            })
    }

    fun deleteProductFromWishlist(productId:Long){
        CraftExchangeRepository
            .getWishlistService()
            .deleteProductsInWishlist(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Wishlist","delete ${t.printStackTrace()}")
                    listener?.onFailure()

                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>) {
                    if(response.isSuccessful){
                        Log.e("Wishlist","delete :"+response.body())
                        listener?.onSuccess()
                    }else{
                        Log.e("Wishlist","delete "+response.body())
                        listener?.onFailure()
                    }
                }
            })
    }

    fun deleteAllProductFromWishlist(){
        CraftExchangeRepository
            .getWishlistService()
            .deleteAllProductsInWishlist(token)
            .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("AddToWishlist failure ","${t.printStackTrace()}")
                    listener?.onFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>) {
                    Log.e(TAG,"onResponse :"+response.code())
                    Log.e(TAG,"onResponse :"+response.isSuccessful)
                    Log.e(TAG,"onResponse :"+call.request().url)
                    if(response.isSuccessful){
                        Log.e(TAG,"addToWishlist :"+response.body())
                        listener?.onSuccess()
                    }else{
                        Log.e(TAG,"addToWishlist "+response.body())
                        listener?.onFailure()
                    }
                }

            })
    }


    companion object{
        const val TAG = "wishlistVM"
    }
}