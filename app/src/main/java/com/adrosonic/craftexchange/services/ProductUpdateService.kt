package com.adrosonic.craftexchange.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

class ProductUpdateService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(ProductUpdateService.KEY_ID)
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        updateProductAction(longId)
    }

    private fun updateProductAction(prodId: Long){
        try {
            if (prodId > 0){
                //todo base upton itemId/product id fire select query for mark deletion to have neccesary parameters to hit delte API
                            }
        }catch (e: Exception){
            Log.e("Exception Deleting",e.message)
        }
    }

    companion object {
        const val KEY_ID = "prod_id"
        private const val JOB_ID = 3000

        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, ProductUpdateService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}