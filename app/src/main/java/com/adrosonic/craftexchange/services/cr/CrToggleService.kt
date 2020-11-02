package com.adrosonic.craftexchange.services.cr

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.Moqs
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.RelatedProducts
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.*
import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchange.repository.data.response.pi.SendPiResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.NotificationViewModel
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList

class CrToggleService: JobIntentService() {

    var _id: Long = 0
    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(KEY_ID)
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        sendCrCall(longId)
    }

    private fun sendCrCall(_idLong: Long){
        try {
            Log.e("Toggle","sendPiCall 222 :$_idLong")
            if (_idLong > 0){
                Log.e("Toggle","sendPiCall :$_idLong")
                val enqId=OrdersPredicates.getEnquiryId(_idLong)
                setCrToggle(enqId)
                }
        }catch (e: Exception){
            Log.e(TAG,"Exception: ${e.message}")
        }
    }


    fun setCrToggle(enquiryId: Long){
        Log.e("Toggle","enquiryId: "+enquiryId)
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getCrService()
            .toggleChangeRequestFromArtisan(token,enquiryId.toInt(),0)
            .enqueue(object : Callback, retrofit2.Callback<DeleteOwnProductRespons> {
                override fun onFailure(call: Call<DeleteOwnProductRespons>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Toggle","isSuccessful false")
                }
                override fun onResponse(
                    call: Call<DeleteOwnProductRespons>,
                    response: Response<DeleteOwnProductRespons>
                ) {
                    Log.e("Toggle","onResponse : ${response?.body()?.data}")
                    if(response?.body()?.valid!!){
                        Log.e("Toggle","isSuccessful ")
                        OrdersPredicates.updateCrStatus(enquiryId)
                    }else{
                        Log.e("Toggle","isSuccessful false")
                    }
                }
            })
    }

    companion object {
        const val KEY_ID = "cr_id"
        private const val JOB_ID = 11000
        private const val TAG="CrService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, CrToggleService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}