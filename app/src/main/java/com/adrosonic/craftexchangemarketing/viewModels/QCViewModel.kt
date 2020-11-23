package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.QcDetails
import com.adrosonic.craftexchangemarketing.database.predicates.QcPredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
//import com.adrosonic.craftexchangemarketing.repository.data.request.qc.SaveOrSendQcRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.BuyerQcResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.QcResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.qc.SaveSendQcResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class QCViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "QCViewModel"
    }
    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

    interface GetQCResponseInterface{
        fun onGetQcFailure()
        fun onGetQcSuccess()
    }

    var getQcListener : GetQCResponseInterface?= null
    val qcList : MutableLiveData<QcDetails> by lazy { MutableLiveData<QcDetails>() }

    fun getQcResponsesbyEnq(enquiryId: Long): MutableLiveData<QcDetails> {
        qcList.value=loadQcResponses(enquiryId)
        return qcList
    }
//
    fun loadQcResponses(enquiryId: Long): QcDetails?{
        var qcList = QcPredicates.getQcResponsesByEnq(enquiryId)
        return qcList
    }
//
//    fun getArtisanQCResponse(enquiryId : Long){
//        CraftExchangeRepository
//            .getQCService()
//            .getArtisanQcResponses(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<QcResponse> {
//                override fun onFailure(call: Call<QcResponse>, t: Throwable) {
//                    getQcListener?.onGetQcFailure()
//                }
//                override fun onResponse(
//                    call: Call<QcResponse>,
//                    response: Response<QcResponse>
//                ) {
//                    if(response.body()?.valid == true){
//                        QcPredicates.insertArtisanQcResponses(response?.body()?.data!!,enquiryId)
//                        getQcListener?.onGetQcSuccess()
//                    }else{
//                        getQcListener?.onGetQcFailure()
//                    }
//                }
//            })
//    }
//
    fun getBuyerQCResponse(enquiryId : Long){
        craftexchangemarketingRepository
            .getQCService()
            .getBuyerQcResponses(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<QcResponse> {
                override fun onFailure(call: Call<QcResponse>, t: Throwable) {
                    getQcListener?.onGetQcFailure()
                }
                override fun onResponse(
                    call: Call<QcResponse>,
                    response: Response<QcResponse>
                ) {
                    if(response.body()?.valid == true){
                        QcPredicates.insertArtisanQcResponses(response?.body()?.data!!,enquiryId)
                        getQcListener?.onGetQcSuccess()
                    }else{
                        getQcListener?.onGetQcFailure()
                    }
                }
            })
    }
//
//    fun saveOrSendQcForm(enquiryId: Long?,form : SaveOrSendQcRequest){
//        CraftExchangeRepository
//            .getQCService()
//            .sendOrSaveQcForm(token,form).enqueue(object : Callback, retrofit2.Callback<SaveSendQcResponse> {
//                override fun onFailure(call: Call<SaveSendQcResponse>, t: Throwable) {
//                    getQcListener?.onGetQcFailure()
//                }
//                override fun onResponse(
//                    call: Call<SaveSendQcResponse>,
//                    response: Response<SaveSendQcResponse>
//                ) {
//                    if(response.body()?.valid == true){
//                        enquiryId?.let { getArtisanQCResponse(it) }
//                    }else{
//                        getQcListener?.onGetQcFailure()
//                    }
//                }
//            })
//    }
}