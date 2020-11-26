package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.escalation.GetNewArtisanEnqRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.FilteredArtisanResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.FilteredArtisans
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.*
import com.adrosonic.craftexchangemarketing.repository.data.response.escalationa.ArtisanData1
import com.adrosonic.craftexchangemarketing.repository.data.response.escalationa.GetNewArtisanEnqResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.ArtisanData
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

    interface GenEnqInterface{
        fun genEnqSuccess(ed : GenerateEnqResponse)
        fun genEnqFailure()
    }

    interface FetchArtisanInterface{
        fun onFetchArtisanFailure()
        fun onFetchArtisanSuccess(list:List<ArtisanData1>?)
    }
    interface FetchFilteredArtisansInterface{
        fun onFilteredArtisanshArtisanFailure()
        fun onFilteredArtisansArtisanSuccess(list:List<FilteredArtisans>?)
    }
    var userListener : UserData?=null
    var countlistener : EscalationCount?=null
    var resolvedListener : EscalationResolve?=null
    var updateListener : EscUpdates?=null
    var chatListener : EscChat?=null
    var paymentListener : EscPayment?=null
    var faultyListener : EscFaulty?=null
    var genEnqListener : GenEnqInterface?=null
    var artisanListener: FetchArtisanInterface? = null
    var filteredArtisanListener: FetchFilteredArtisansInterface? = null

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

    fun createNewEnquiry(enquiryId: Long){
        Log.e("EscalationVM","fn called ")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEscalationDataService()
            .createNewEnquiry(token,enquiryId,"android")
            .enqueue(object: Callback, retrofit2.Callback<GenerateEnqResponse> {
                override fun onFailure(call: Call<GenerateEnqResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("EscalationVM","fail: ")
                    genEnqListener?.genEnqFailure()
                }
                override fun onResponse(
                    call: Call<GenerateEnqResponse>,
                    response: retrofit2.Response<GenerateEnqResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("EscalationVM","UPdate: "+response.body()?.data)
                        genEnqListener?.genEnqSuccess(response.body()!!)
                    }else{
                        Log.e("EscalationVM","false ")
                        genEnqListener?.genEnqFailure()
                    }
                }

            })
    }

    fun getArtisansLessThan8Rating(clusterId: Long,searchStr:String,enqId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"clusterId: $clusterId")
        Log.e(TAG,"searchStr: $searchStr")
        Log.e(TAG,"enqId: $enqId")
        Log.e(TAG,"GetNewArtisanEnqRequest: ${GetNewArtisanEnqRequest(clusterId.toInt(),enqId.toInt(),searchStr)}")
        craftexchangemarketingRepository
            .getEscalationDataService()
            .getArtisansForNewEnquiry(token, GetNewArtisanEnqRequest(clusterId.toInt(),enqId.toInt(),searchStr))
            .enqueue(object: Callback, retrofit2.Callback<GetNewArtisanEnqResponse> {
                override fun onFailure(call: Call<GetNewArtisanEnqResponse>, t: Throwable) {
                    t.printStackTrace()
                    artisanListener?.onFetchArtisanFailure()
                    Log.e(TAG,"onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<GetNewArtisanEnqResponse>,
                    response: retrofit2.Response<GetNewArtisanEnqResponse>) {
                    if(response.body()?.valid!!){
                        Log.e(TAG,"onResponse if : ${response.body()?.data?.size}")
                        artisanListener?.onFetchArtisanSuccess(response?.body()?.data)
                    }else{
                        artisanListener?.onFetchArtisanFailure()
                        Log.e(TAG,"onResponse else : ${response.body()?.valid}")
                    }
                }

            })
    }

    fun getFilteredArtisans(clusterId: Int,searchStr:String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductCatService()
            .getFilteredArtisans(token,clusterId,searchStr)
            .enqueue(object: Callback, retrofit2.Callback<FilteredArtisanResponse> {
                override fun onFailure(call: Call<FilteredArtisanResponse>, t: Throwable) {
                    t.printStackTrace()
                    filteredArtisanListener?.onFilteredArtisanshArtisanFailure()
                    Log.e("getFilteredArtisans","onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<FilteredArtisanResponse>,
                    response: retrofit2.Response<FilteredArtisanResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("getFilteredArtisans","onSuccess: ${response?.body()}")
                        filteredArtisanListener?.onFilteredArtisansArtisanSuccess(response.body()?.data)

                    }else{
                        filteredArtisanListener?.onFilteredArtisanshArtisanFailure()
                        Log.e("ArtisanProduct","onFailure: "+response.body()?.errorCode)
                    }
                }

            })
    }
}