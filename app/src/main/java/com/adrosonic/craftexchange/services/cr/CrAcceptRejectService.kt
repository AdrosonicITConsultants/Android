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
import com.adrosonic.craftexchange.repository.data.request.changeRequest.RaiseCrInput
import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrOptionsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchange.repository.data.response.pi.SendPiResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.NotificationViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

class CrAcceptRejectService: JobIntentService() {

    var _id: Long = 0
    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(KEY_ID)
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        sendCrCall(longId)
    }

    private fun sendCrCall(_idLong: Long){
        try {
            Log.e("RaiseCr","acceptRejectChangeRequest 222 :$_idLong")
            if (_idLong > 0){
                Log.e("RaiseCr","acceptRejectChangeRequest :$_idLong")
                val enqId=OrdersPredicates.getEnquiryId(_idLong)
                val orderDetails=OrdersPredicates.getSingleOnGoOrderDetails(enqId,0)
                val gson = GsonBuilder().create()
                var crInput = gson.fromJson(orderDetails?.crStatusUpdateInput, RaiseCrInput::class.java)
                Log.e("RaiseCr","crInput :${crInput.itemList.size}")
                acceptRejectChangeRequest(crInput,orderDetails?.changeRequestStatus?:0)
                }
        }catch (e: Exception){
            Log.e("RaiseCr","Exception: ${e.message}")
        }
    }


    fun acceptRejectChangeRequest(changeRequestParameters : RaiseCrInput, changeRequestStatus:Long){
        Log.e("RaiseCr","changeRequestStatus : $changeRequestStatus ")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getCrService()
            .changeRequestStatusUpdate(token,changeRequestParameters,changeRequestStatus.toInt())
            .enqueue(object : Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("RaiseCr","isSuccessful false")
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: Response<NotificationReadResponse>
                ) {
                    Log.e("RaiseCr","onResponse : ${response?.body()?.data}")
                    val valid=response.body()?.valid?:false
                    if (valid) {
                        Log.e("RaiseCr","isSuccessful ")
//                        CrPredicates.updatePostCrStatus(changeRequestParameters,changeRequestStatus)/
                        OrdersPredicates.updateChangeRequestStatusOffline(changeRequestParameters.enquiryId,"",0,changeRequestStatus)

                    }else{
                        Log.e("RaiseCr","isSuccessful false")
                    }
                }
            })
    }


    companion object {
        const val KEY_ID = "cr_id"
        private const val JOB_ID = 12000
        private const val TAG="CrAcceptRejectService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, CrAcceptRejectService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}