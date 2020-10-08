package com.adrosonic.craftexchangemarketing.services.moq

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.RelatedProducts
import com.adrosonic.craftexchangemarketing.database.predicates.*
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate.*
import com.adrosonic.craftexchangemarketing.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryViewModel
import com.adrosonic.craftexchangemarketing.viewModels.NotificationViewModel
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

class MoqSendService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(KEY_ID)
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        sendMoqCall(longId)
    }

    private fun sendMoqCall(_id: Long){
        try {
            if (_id > 0){
                Log.e("Offline","moqId :$_id")
                val moqObj=MoqsPredicates.getSingleMoqForOffline(_id)
                sendMoq(moqObj?.enquiryId?:0,moqObj?.additionalInfo?:"",moqObj?.deliveryTimeId?:0,moqObj?.moq?:0,moqObj?.ppu?:"",_id)
                }
        }catch (e: Exception){
            Log.e(TAG,"Exception: ${e.message}")
        }
    }

    fun sendMoq(enquiryId:Long,additionalInfo: String,deliveryTimeID: Long,  moq: Long,ppu: String,_id:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        val request= SendMoqRequest(additionalInfo,deliveryTimeID,moq,ppu)
        Log.e(EnquiryViewModel.TAG,"sendMoq enquiryId :$enquiryId")
        Log.e(EnquiryViewModel.TAG,"sendMoq request :${request.additionalInfo}")
        Log.e(EnquiryViewModel.TAG,"sendMoq request :${request.deliveryTimeId}")
        Log.e(EnquiryViewModel.TAG,"sendMoq request :${request.ppu}")
        Log.e(EnquiryViewModel.TAG,"sendMoq request :${request.moq}")
        craftexchangemarketingRepository
            .getMoqService()
            .sendMoq(token,enquiryId.toInt(),request).enqueue(object : Callback, retrofit2.Callback<SendMoqResponse> {
                override fun onFailure(call: Call<SendMoqResponse>, t: Throwable) {
                    Log.e(EnquiryViewModel.TAG,"sendMoq :${t.message}")
                    Log.e(EnquiryViewModel.TAG,"sendMoq :${t.localizedMessage}")
                    Log.e(EnquiryViewModel.TAG,"sendMoq :${t.stackTrace}")
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<SendMoqResponse>,
                    response: Response<SendMoqResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(EnquiryViewModel.TAG,"sendMoq :$valid")
                    Log.e(EnquiryViewModel.TAG,"sendMoq :${response.body()?.errorMessage}")
                    if(valid){
                        Log.e(EnquiryViewModel.TAG,"sendMoq :${response.body()?.data?.moq?.additionalInfo}")
                        MoqsPredicates.updateMoqsPostSend(response.body()?.data?.moq!!,response.body()?.data?.accepted?:false,enquiryId,_id)
                    }
                }
            })
    }

    companion object {
        const val KEY_ID = "prod_id"
        private const val JOB_ID = 8000
        private const val TAG="MoqSendService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, MoqSendService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}