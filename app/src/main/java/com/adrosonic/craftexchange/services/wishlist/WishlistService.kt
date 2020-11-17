package com.adrosonic.craftexchange.services.wishlist

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.ResponseBody
import retrofit2.Call
import javax.security.auth.callback.Callback

class WishlistService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(WishlistService.KEY_ID)
//        if(itemId.isEmpty())itemId="-1"
//        var longId=itemId.toLong()
        actionToWishlist(itemId.toLong())
    }

    private fun actionToWishlist(productId : Long){
        try {
            var productDetails = WishlistPredicates.getProductWishlisting(productId)
            when(productDetails?.isWishlisted ){
                0L -> {
                    removeProductFromWishlist(productId)
                }
                1L -> {
                    addProductToWishlist(productId)
                }
            }

        }catch (e: Exception){
            Log.e("Exception Adding",e.message)
        }
    }

    fun addProductToWishlist(productId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        CraftExchangeRepository
            .getWishlistService()
            .addToWishlist(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("AddToWishlist failure ","${t.printStackTrace()}")
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>) {
                    Log.e(TAG,"onResponse :"+response.code())
                    Log.e(TAG,"onResponse :"+response.isSuccessful)
                    Log.e(TAG,"onResponse :"+call.request().url)
                    if(response.isSuccessful){
                        Log.e(TAG,"addToWishlist :"+response.body())
                        WishlistPredicates.updateProductWishlisting(productId,1L,0L)
                    }else{
                        Log.e(TAG,"addToWishlist "+response.body())
                    }
                }

            })
    }


    fun removeProductFromWishlist(productId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        CraftExchangeRepository
            .getWishlistService()
            .deleteProductsInWishlist(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("RemoveWishlist failure ","${t.printStackTrace()}")
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>) {
                    Log.e(TAG,"onResponse :"+response.code())
                    Log.e(TAG,"onResponse :"+response.isSuccessful)
                    Log.e(TAG,"onResponse :"+call.request().url)
                    if(response.isSuccessful){
                        Log.e(TAG,"REmoveFromWishlist :"+response.body())
                        WishlistPredicates.updateProductWishlisting(productId,0L,0L)
                    }else{
                        Log.e(TAG,"REmoveFromWishlist "+response.body())
                    }
                }

            })
    }

    companion object {
        const val KEY_ID = "prod_id"
        private const val JOB_ID = 4000
        private const val TAG="WishlistAddService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, WishlistService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}