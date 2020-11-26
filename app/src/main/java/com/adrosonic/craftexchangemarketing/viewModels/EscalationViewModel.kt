package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.*
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import javax.security.auth.callback.Callback

class EscalationViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "EscalationViewModel"
    }
    interface EscalationResolve{
        fun escalationResolved(m :String)
    }
    interface EscalationCount{
        fun updateCount(c :Long)
    }
    interface UserData{
        fun contacdetails(cd : ArrayList<userData>)
    }
    interface EscUpdates{
        fun EscUpdateSuccess(ed : ArrayList<EscalationData>)
    }
    interface EscChat{
        fun EscChatSuccess(ed : ArrayList<EscalationData>)
    }
    interface EscPayment{
        fun EscPaymentSuccess(ed : ArrayList<EscalationData>)
    }
    interface EscFaulty{
        fun EscFaultySuccess(ed : ArrayList<EscalationData>)
    }
    var userListener : UserData?=null
    var countlistener : EscalationCount?=null
    var resolvedListener : EscalationResolve?=null
    var updateListener : EscUpdates?=null
    var chatListener : EscChat?=null
    var paymentListener : EscPayment?=null
    var faultyListener : EscFaulty?=null


    fun escUpdates(pageNo: Long, searchStr: String?){
        Log.e("EscalationVM","fn called ")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEscalationDataService()
            .getEscUpdates(token,"6",pageNo,"date","Desc",searchStr)
            .enqueue(object: Callback, retrofit2.Callback<EscalationResponse> {
                override fun onFailure(call: Call<EscalationResponse>, t: Throwable) {
                    t.printStackTrace()
//                    listener?.onFailedEnquiryGeneration()
//                    Log.e("Enquiry Generation","Failure: "+t.message)
                    Log.e("EscalationVM","fail: ")

                }
                override fun onResponse(
                    call: Call<EscalationResponse>,
                    response: retrofit2.Response<EscalationResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("EscalationVM","UPdate: "+response.body()?.data)
                        response.body()?.data?.let { updateListener?.EscUpdateSuccess(it) }

//
                    }else{
                        Log.e("EscalationVM","false ")

//                        listener?.onFailedEnquiryGeneration()
//                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }
    fun escUpdatesCount(category : String , searchStr: String?){
        Log.e("EscalationVM","fn called ")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEscalationDataService()
            .getEscalationCount(token,category,"date","Desc",searchStr)
            .enqueue(object: Callback, retrofit2.Callback<EscalationCountResponse> {
                override fun onFailure(call: Call<EscalationCountResponse>, t: Throwable) {
                    t.printStackTrace()
//                    listener?.onFailedEnquiryGeneration()
//                    Log.e("Enquiry Generation","Failure: "+t.message)
                    Log.e("EscalationVM","fail: ")

                }
                override fun onResponse(
                    call: Call<EscalationCountResponse>,
                    response: retrofit2.Response<EscalationCountResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("EscalationVM","UPdate: "+response.body()?.data)
                        response.body()?.data?.let { countlistener?.updateCount(it) }

//
                    }else{
                        Log.e("EscalationVM","false ")

//                        listener?.onFailedEnquiryGeneration()
//                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun chatUpdates(pageNo: Long, searchStr: String?){
        Log.e("EscalationVM","fn called ")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEscalationDataService()
            .getEscUpdates(token,"4,5",pageNo,"date","Desc",searchStr)
            .enqueue(object: Callback, retrofit2.Callback<EscalationResponse> {
                override fun onFailure(call: Call<EscalationResponse>, t: Throwable) {
                    t.printStackTrace()
//                    listener?.onFailedEnquiryGeneration()
//                    Log.e("Enquiry Generation","Failure: "+t.message)
                    Log.e("EscalationVMC","fail: ")

                }
                override fun onResponse(
                    call: Call<EscalationResponse>,
                    response: retrofit2.Response<EscalationResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("EscalationVMC","UPdate: "+response.body()?.data)
                        response.body()?.data?.let { chatListener?.EscChatSuccess(it) }

//
                    }else{
                        Log.e("EscalationVMC","false ")

//                        listener?.onFailedEnquiryGeneration()
//                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun PaymentUpdates(pageNo: Long, searchStr: String?){
        Log.e("EscalationVM","fn called ")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEscalationDataService()
            .getEscUpdates(token,"1",pageNo,"date","Desc",searchStr)
            .enqueue(object: Callback, retrofit2.Callback<EscalationResponse> {
                override fun onFailure(call: Call<EscalationResponse>, t: Throwable) {
                    t.printStackTrace()
//                    listener?.onFailedEnquiryGeneration()
//                    Log.e("Enquiry Generation","Failure: "+t.message)
                    Log.e("EscalationVMC","fail: ")

                }
                override fun onResponse(
                    call: Call<EscalationResponse>,
                    response: retrofit2.Response<EscalationResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("EscalationVMC","UPdate: "+response.body()?.data)
                        response.body()?.data?.let { paymentListener?.EscPaymentSuccess(it) }

//
                    }else{
                        Log.e("EscalationVMC","false ")

//                        listener?.onFailedEnquiryGeneration()
//                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun FaultyUpdates(pageNo: Long, searchStr: String?){
        Log.e("EscalationVM","fn called ")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEscalationDataService()
            .getEscUpdates(token,"2,3,7",pageNo,"date","Desc",searchStr)
            .enqueue(object: Callback, retrofit2.Callback<EscalationResponse> {
                override fun onFailure(call: Call<EscalationResponse>, t: Throwable) {
                    t.printStackTrace()
//                    listener?.onFailedEnquiryGeneration()
//                    Log.e("Enquiry Generation","Failure: "+t.message)
                    Log.e("EscalationVMC","fail: ")

                }
                override fun onResponse(
                    call: Call<EscalationResponse>,
                    response: retrofit2.Response<EscalationResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("EscalationVMC","UPdate: "+response.body()?.data)
                        response.body()?.data?.let { faultyListener?.EscFaultySuccess(it) }

//
                    }else{
                        Log.e("EscalationVMC","false ")

//                        listener?.onFailedEnquiryGeneration()
//                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun userDetails(enquiryId: Long){
        Log.e("EscalationVM","fn called ")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEscalationDataService()
            .getUserDetails(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<UserDataResponse> {
                override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                    t.printStackTrace()
//                    listener?.onFailedEnquiryGeneration()
//                    Log.e("Enquiry Generation","Failure: "+t.message)
                    Log.e("EscalationVM","fail: ")

                }
                override fun onResponse(
                    call: Call<UserDataResponse>,
                    response: retrofit2.Response<UserDataResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("EscalationVM","UPdate: "+response.body()?.data)
//                        response.body()?.data?.let { updateListener?.EscUpdateSuccess(it) }
                        response.body()?.data?.let { userListener?.contacdetails(it) }
//
                    }else{
                        Log.e("EscalationVM","false ")

//                        listener?.onFailedEnquiryGeneration()
//                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun markResolved(escalationId: Long){
        Log.e("EscalationVM","fn called ")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEscalationDataService()
            .markResolved(token,escalationId)
            .enqueue(object: Callback, retrofit2.Callback<ResolvedEscalationResponse> {
                override fun onFailure(call: Call<ResolvedEscalationResponse>, t: Throwable) {
                    t.printStackTrace()
//                    listener?.onFailedEnquiryGeneration()
//                    Log.e("Enquiry Generation","Failure: "+t.message)
                    Log.e("EscalationVM","fail: ")

                }
                override fun onResponse(
                    call: Call<ResolvedEscalationResponse>,
                    response: retrofit2.Response<ResolvedEscalationResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("EscalationVM","UPdate: "+response.body()?.data)
//                        response.body()?.data?.let { updateListener?.EscUpdateSuccess(it) }
                        response.body()?.data?.let { resolvedListener?.escalationResolved(it) }
//
                    }else{
                        Log.e("EscalationVM","false ")

//                        listener?.onFailedEnquiryGeneration()
//                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

}