package com.adrosonic.craftexchange.services.qc

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.entities.realmEntities.QcDetails
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.database.predicates.QcPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.request.qc.QuestionAnswer
import com.adrosonic.craftexchange.repository.data.request.qc.SaveOrSendQcRequest
import com.adrosonic.craftexchange.repository.data.request.qc.sSaveOrSendQcRequest
import com.adrosonic.craftexchange.repository.data.response.pi.SendPiResponse
import com.adrosonic.craftexchange.repository.data.response.qc.SaveSendQcResponse
import com.adrosonic.craftexchange.services.pi.PiService
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class QcService: JobIntentService() {

    var qcObj: QcDetails? = null
    var _id: Long = 0
    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(KEY_ID)
        Log.e(TAG,"onHandleWork 000 :$itemId")
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        Log.e(TAG,"onHandleWork 111 :$longId")
        sendQcCall(longId)
    }

    private fun sendQcCall(_idLong: Long){
        try {
            Log.e(TAG,"sendQcCall 222 :$_idLong")
            if (_idLong > 0){
                _id=_idLong
                Log.e(TAG,"sendQcCall :$_idLong")
                qcObj= QcPredicates.getQcById(_idLong)

                var qc = SaveOrSendQcRequest()
                var request = Gson().fromJson(qcObj?.qcResquestString,SaveOrSendQcRequest::class.java)
                qc.enquiryId = request.enquiryId?:0
//                qc.questionAnswers = quesAnsList
//                qc.saveOrSend = qcObj?.isSend?:0
//                qc.stageId = qcObj?.stageID?:0
                saveOrSendQcForm(qc.enquiryId?:0,request)
            }

        }catch (e: Exception){
            Log.e(TAG,"Exception: ${e.message}")
        }
    }


    fun saveOrSendQcForm(enquiryId: Long?,form : SaveOrSendQcRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getQCService()
            .sendOrSaveQcForm(token,form).enqueue(object : Callback, retrofit2.Callback<SaveSendQcResponse> {
                override fun onFailure(call: Call<SaveSendQcResponse>, t: Throwable) {
                    Log.e(TAG,"sendQC Fail :${t.message}")
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<SaveSendQcResponse>,
                    response: Response<SaveSendQcResponse>
                ) {
                    if(response.body()?.valid == true){
                        enquiryId?.let { QcPredicates?.updateQcPostSendSave(it) }
                    }else{
                        Log.e(TAG,"sendQC Fail :${response?.body()?.errorMessage}")
                    }
                }
            })
    }

    companion object {
        const val KEY_ID = "enquiryID"
        private const val JOB_ID = 90320
        private const val TAG="QcService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, QcService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}