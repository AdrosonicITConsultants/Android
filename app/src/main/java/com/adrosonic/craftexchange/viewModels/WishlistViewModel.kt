package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
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

    fun addProductToWishlist(productId:Long){
        CraftExchangeRepository
            .getWishlistService()
            .addToWishlist(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("AddToWishlist failure ","${t.printStackTrace()}")
                    Utility.displayMessage("Error while adding to wishlist",getApplication())
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>) {
                    if(response.isSuccessful){
                        Log.e(TAG,"addToWishlist :"+response.body())
                    }else{
                        Log.e(TAG,"addToWishlist "+response.body())
                        Utility.displayMessage("Error while adding to wishlist",getApplication())

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