package com.adrosonic.craftexchange.services.pi

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

class PiService: JobIntentService() {

    var piObj: PiDetails? = null
    var isSend: Long = 0
    var isSave: Long = 0
    var _id: Long = 0
    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(KEY_ID)
        Log.e(TAG,"onHandleWork 000 :$itemId")
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        Log.e(TAG,"onHandleWork 111 :$longId")
        sendPiCall(longId)
    }

    private fun sendPiCall(_idLong: Long){
        try {
            Log.e(TAG,"sendPiCall 222 :$_idLong")
            if (_idLong > 0){
                _id=_idLong
                Log.e(TAG,"sendPiCall :$_idLong")
                piObj=PiPredicates.getPiById(_idLong)
                var pi=SendPiRequest()
                pi.cgst=piObj?.cgst?:0
                pi.expectedDateOfDelivery= piObj?.date?:""
                pi.hsn=piObj?.hsn?:0
                pi.ppu=  piObj?.ppu?:0
                pi.quantity=piObj?.quantity?:0
                pi.sgst=piObj?.sgst?:0
                savePi(piObj?.enquiryId?:0,pi)
                isSend=piObj?.actionMarkPiForSend?:0
                isSave=piObj?.actionMarkPiForSave?:0
                }
        }catch (e: Exception){
            Log.e(TAG,"Exception: ${e.message}")
        }
    }


    fun savePi(enquiryId:Long,pi: SendPiRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"sendPi :${enquiryId}")
        CraftExchangeRepository
            .getPiService()
            .savePI(token,enquiryId.toInt(),pi).enqueue(object : Callback, retrofit2.Callback<SendPiResponse> {
                override fun onFailure(call: Call<SendPiResponse>, t: Throwable) {
                    Log.e(TAG,"sendPi :${t.message}")
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<SendPiResponse>,
                    response: Response<SendPiResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"sendPi :$valid")
                    if(valid){
                        Log.e(TAG,"sendPi :${response.body()?.data}")
                        response.body()?.data?.let {
                            if(isSend.equals(1L)){
                                sendPi(enquiryId,pi)
                            }else{
                                PiPredicates.updatePiPostSelection(enquiryId,0,null)
                            }
                        }
                    }
                }
            })
    }

    fun sendPi(enquiryId:Long,pi: SendPiRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"sendPi :${enquiryId}")
        CraftExchangeRepository
            .getPiService()
            .sendPI(token,enquiryId.toInt(),pi).enqueue(object : Callback, retrofit2.Callback<SendPiResponse> {
                override fun onFailure(call: Call<SendPiResponse>, t: Throwable) {
                    Log.e(TAG,"sendPi :${t.message}")
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<SendPiResponse>,
                    response: Response<SendPiResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"sendPi :$valid")
                    if(valid){
                        Log.e(TAG,"sendPi :${response.body()?.data}")
                        response.body()?.data?.let {
//                            PiPredicates.updatePiPostSelection(enquiryId,null,0)
                            PiPredicates.deletePi(_id)
                        }
                    }
                }
            })
    }

    companion object {
        const val KEY_ID = "pi_id"
        private const val JOB_ID = 7000
        private const val TAG="PiService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, PiService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}