package com.adrosonic.craftexchange.services.taxInv

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.TaxInvDetails
import com.adrosonic.craftexchange.database.predicates.OrdersPredicates
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.database.predicates.TaxInvPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.pi.SendPiResponse
import com.adrosonic.craftexchange.repository.data.response.taxInv.TaxInvoiceResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class TaxInvService: JobIntentService() {

    var tiObj: TaxInvDetails? = null
    var isSend: Long = 0
    var _id: Long = 0
    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(KEY_ID)
        Log.e(TAG,"onHandleWork 000 :$itemId")
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        Log.e(TAG,"onHandleWork 111 :$longId")
        sendTiCall(longId)
    }

    private fun sendTiCall(_idLong: Long){
        try {
            Log.e(TAG,"sendTiCall 222 :$_idLong")
            if (_idLong > 0){
                _id=_idLong
                Log.e(TAG,"sendTiCall :$_idLong")
                tiObj= TaxInvPredicates.getTiById(_idLong)
                var ti= SendTiRequest()
                ti.cgst=tiObj?.cgst.toString()
                ti.sgst= tiObj?.sgst.toString()
                ti.enquiryId=tiObj?.enquiryID.toString()
                ti.ppu=  tiObj?.ppu?:0
                ti.quantity=tiObj?.quantity?:0
                ti.finalTotalAmt=tiObj?.finalTotalAmt ?:0
                ti.advancePaidAmt=tiObj?.advancePaidAmt?.toString()
                ti.deliveryCharges=tiObj?.sgst?.toString()

                if(tiObj?.actionMarkTiForSend!=null){
                   generateTaxInvoice(ti)
                }
            }
        }catch (e: Exception){
            Log.e(TAG,"Exception: ${e.message}")
        }
    }

    fun generateTaxInvoice(invoiceRequest : SendTiRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getTiService()
            .generateTaxInvoice(token,invoiceRequest)
            .enqueue(object : Callback, retrofit2.Callback<TaxInvoiceResponse> {
                override fun onFailure(call: Call<TaxInvoiceResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<TaxInvoiceResponse>,
                    response: Response<TaxInvoiceResponse>
                ) {
                    if(response?.body()?.valid!!){
                        TaxInvPredicates.insertTi(response.body()!!)
                    }
                }
            })
    }



    companion object {
        const val KEY_ID = "enquiryID"
        private const val JOB_ID = 67800
        private const val TAG="TaxInvService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, TaxInvService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}