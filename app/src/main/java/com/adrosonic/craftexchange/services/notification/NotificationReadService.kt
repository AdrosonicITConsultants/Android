package com.adrosonic.craftexchange.services.notification

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.RelatedProducts
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.*
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.viewModels.NotificationViewModel
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList

class NotificationReadService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(NotificationReadService.KEY_ID)
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        updateNotfication(longId)
    }

    private fun updateNotfication(notificationId: Long){
        try {
            if (notificationId > 0){
                Log.e("Offline","notificationId :$notificationId")
                    markSingleNotificationAsRead(notificationId)
                }
        }catch (e: Exception){
            Log.e(TAG,"Exception: ${e.message}")
        }
    }

    fun markSingleNotificationAsRead(notificationId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getNotificationService()
            .markSingleNotificationAsRead(token,notificationId)
            .enqueue(object: Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e(TAG,"onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: retrofit2.Response<NotificationReadResponse>) {
                    Log.e(TAG,"getProductUploadData onResponse: "+response.body()?.valid)
                    if(response.body()?.valid == true){
                      NotificationPredicates.updateNoificationPostRead(notificationId)
                    }
                }
            })
    }

    companion object {
        const val KEY_ID = "prod_id"
        private const val JOB_ID = 10000
        private const val TAG="NotificationReadService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, NotificationReadService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}