package com.adrosonic.craftexchangemarketing.services.customDesign

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchangemarketing.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.ProductImagePredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import javax.security.auth.callback.Callback


class DeleteOwnProductService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(KEY_ID)
        if (itemId.isEmpty()) itemId = "-1"
        var longId = itemId.toLong()
        deleteCustomProductAtion(longId)
    }

    private fun deleteCustomProductAtion(prodId: Long) {
        try {
            if (prodId > 0) {
                Log.e("Offline", "prodId :" + prodId)
                val productEntry = BuyerCustomProductPredicates.getProductId(prodId)
                deleteProduct(prodId)
            }
        } catch (e: Exception) {
            Log.e("Offline", "Exception " + e.localizedMessage)
        }
    }


    fun deleteProduct( prodId: Long) {
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN, "")}"
        Log.e("Offline", "prodId :" + prodId)
        craftexchangemarketingRepository
            .getBuyerOwnDesignService()
            .deleteOwnDesignProducts(token,prodId.toInt())
            .enqueue(object: Callback, retrofit2.Callback<DeleteOwnProductRespons?> {
                override fun onFailure(call: Call<DeleteOwnProductRespons?>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("deleteProduct","onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<DeleteOwnProductRespons?>,
                    response: retrofit2.Response<DeleteOwnProductRespons?>) {
                    val res=response.body()
                    Log.e("deleteProduct", "onResponse: " + res?.valid)
                    if(res!=null) {
                        if (res.valid) {
                            deleteOfflineEntries(prodId)
                        }
                    }
                }

            })
    }


    private fun deleteOfflineEntries(prodId: Long) {
        BuyerCustomProductPredicates.deleteProductEntry(prodId)
//        RelateProductPredicates.deleteRelatedProduct(prodId)
        ProductImagePredicates.deleteProdImages(prodId)
//        WeaveTypesPredicates.deleteWeaveIds(prodId)
//        ProductCaresPredicates.deleteCareIds(prodId)
    }


    companion object {
        const val KEY_ID = "own_prod_id"
        private const val JOB_ID = 6000
        private const val TAG = "DeleteOwnProductService"
        fun enqueueWork(context: Context, work: Intent) {
            try {
                enqueueWork(context, DeleteOwnProductService::class.java, this.JOB_ID, work)
            } catch (e: Exception) {
                Log.e("EnqueueWork Delete", e.message)
            }
        }
    }
}
