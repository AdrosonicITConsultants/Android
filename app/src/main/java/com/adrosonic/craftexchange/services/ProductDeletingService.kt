package com.adrosonic.craftexchange.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import javax.security.auth.callback.Callback

class ProductDeletingService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var prodId = intent.getStringExtra(ProductDeletingService.KEY_ID)
        if(prodId.isEmpty())prodId="-1"
        var longId=prodId.toLong()
        Log.e("Offline", "onHandleWork longId :" +longId)
        deleteAction(longId)
    }

    private fun deleteAction(prodId: Long){
        try {
            if (prodId > 0){
                val productEntry=ProductPredicates.getArtisanProductsId(prodId)
//                productEntry?.productId?.let { deleteProduct(it) }
                Log.e("Offline", "deleteAction prodId :" + productEntry)
                deleteProduct(productEntry?:0)
            }
        }catch (e: Exception){
            Log.e("Exception Deleting",e.message)
        }
    }

    fun deleteProduct( prodId: Long?) {
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN, "")}"
        Log.e("Offline", "deleteProduct prodId :" + prodId)
        CraftExchangeRepository
            .getProductService()
            .deleteProductsTemplate(token,prodId!!.toInt())
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
                        if (res!!.valid) {
                            deleteOfflineEntries(prodId)
                        }
                    }
                }

            })
    }


    private fun deleteOfflineEntries(prodId: Long) {
        BuyerCustomProductPredicates.deleteProductEntry(prodId)
        RelateProductPredicates.deleteRelatedProduct(prodId)
        ProductImagePredicates.deleteProdImages(prodId)
        WeaveTypesPredicates.deleteWeaveIds(prodId)
        ProductCaresPredicates.deleteCareIds(prodId)
    }

    companion object {
        const val KEY_ID = "prod_id"
        private const val JOB_ID = 2000

        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, ProductDeletingService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}