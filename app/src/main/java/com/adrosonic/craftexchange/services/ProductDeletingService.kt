package com.adrosonic.craftexchange.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

class ProductDeletingService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var prodId = intent.getStringExtra(ProductDeletingService.KEY_ID)
        if(prodId.isEmpty())prodId="-1"
        var longId=prodId.toLong()
        deleteAction(longId)
    }

    private fun deleteAction(prodId: Long){
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