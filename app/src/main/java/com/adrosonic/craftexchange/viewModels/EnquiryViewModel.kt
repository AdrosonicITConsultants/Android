package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.ResponseBody
import retrofit2.Call
import javax.security.auth.callback.Callback

class EnquiryViewModel(application: Application) : AndroidViewModel(application){

    interface GenerateEnquiryInterface{
        fun onSuccessEnquiryGeneration(enquiry : GenerateEnquiryResponse)
        fun onExistingEnquiryGeneration(productName : String, id : String, code : String)
        fun onFailedEnquiryGeneration()
    }

    interface FetchEnquiryInterface{
        fun onFailure()
        fun onSuccess()
    }

    val ongoingEnqList : MutableLiveData<RealmResults<OngoingEnquiries>> by lazy { MutableLiveData<RealmResults<OngoingEnquiries>>() }
    val onGoEnqDetails : MutableLiveData<OngoingEnquiries> by lazy { MutableLiveData<OngoingEnquiries>() }

    val compEnqList : MutableLiveData<RealmResults<CompletedEnquiries>> by lazy { MutableLiveData<RealmResults<CompletedEnquiries>>() }
    val compEnqDetails : MutableLiveData<CompletedEnquiries> by lazy { MutableLiveData<CompletedEnquiries>() }

    var listener: GenerateEnquiryInterface ?= null
    var fetchEnqListener : FetchEnquiryInterface ?= null

    fun getOnEnqListMutableData(): MutableLiveData<RealmResults<OngoingEnquiries>> {
        ongoingEnqList.value=loadOnEnqList()
        return ongoingEnqList
    }

    fun loadOnEnqList(): RealmResults<OngoingEnquiries>?{
        var ongoingEnqList = EnquiryPredicates.getAllOngoingEnquiries()
        Log.e("ongoingEnqList","ongoingEnqList :"+ongoingEnqList?.size)
        return ongoingEnqList
    }

    fun getSingleOnEnqData(enqId : Long): MutableLiveData<OngoingEnquiries> {
        onGoEnqDetails.value=loadSingleEnqDetails(enqId)
        return onGoEnqDetails
    }

    fun loadSingleEnqDetails(enqId : Long): OngoingEnquiries?{
        var enquiryDetails = EnquiryPredicates.getSingleOnGoEnquiryDetails(enqId)
//        Log.e("enquiryDetails","enquiryDetails :"+ongoingEnqList?.size)
        return enquiryDetails
    }





    fun getCompEnqListMutableData(): MutableLiveData<RealmResults<CompletedEnquiries>> {
        compEnqList.value=loadCompEnqList()
        return compEnqList
    }

    fun loadCompEnqList(): RealmResults<CompletedEnquiries>?{
        var compEnqList = EnquiryPredicates.getAllCompletedEnquiries()
        Log.e("compEnqList","compEnqList :"+compEnqList?.size)
        return compEnqList
    }

    fun getSingleCompEnqData(enqId : Long): MutableLiveData<CompletedEnquiries> {
        compEnqDetails.value=loadSingleCEnqDetails(enqId)
        return compEnqDetails
    }

    fun loadSingleCEnqDetails(enqId : Long): CompletedEnquiries?{
        return EnquiryPredicates.getSingleCompEnquiryDetails(enqId)
    }


    fun ifEnquiryExists(productId : Long,isCustom : Boolean){

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .ifEnquiryExists(token,productId,isCustom)
            .enqueue(object: Callback, retrofit2.Callback<IfExistEnquiryResponse> {
                override fun onFailure(call: Call<IfExistEnquiryResponse>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailedEnquiryGeneration()
                    Log.e("Enquiry Generation","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<IfExistEnquiryResponse>,
                    response: retrofit2.Response<IfExistEnquiryResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Enquiry Generation","Success: "+response.body()?.errorMessage)
                        if(response.body()?.data?.ifExists == true){
                            response.body()?.data?.enquiryId?.let {
//                            TODO : save all the enquiries after login ..into DB
                                listener?.onExistingEnquiryGeneration(
                                    response.body()?.data?.productName.toString(),
                                    response.body()?.data?.enquiryId.toString(),
                                    response.body()?.data?.code.toString())
                            }

                        }else{
                            generateEnquiry(productId,isCustom,"Android")
                        }
                    }else{
                        listener?.onFailedEnquiryGeneration()
                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun generateEnquiry(productId : Long, isCustom : Boolean, deviceName : String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .generateEnquiry(token,productId,isCustom,deviceName)
            .enqueue(object: Callback, retrofit2.Callback<GenerateEnquiryResponse> {
                override fun onFailure(call: Call<GenerateEnquiryResponse>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailedEnquiryGeneration()
                    Log.e("Enquiry Generation","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<GenerateEnquiryResponse>,
                    response: retrofit2.Response<GenerateEnquiryResponse>) {

                    if(response.body()?.valid == true){
//                        if(response?.body()?.data?.ifExists == true){
//                            TODO : save all the enquiries after login ..into DB
//                            var exEnq = EnquiryPredicates?.getExistingEnquiryDetails(productId,true)
//                            exEnq?.let { listener?.onExistingEnquiryGeneration(it) }
                        Log.e("Enquiry Generation","Success: "+response.body()?.errorMessage)

                        //TODO to be discussed
//                        EnquiryPredicates.insertBuyerEnquiries(response.body()!!)
                        listener?.onSuccessEnquiryGeneration(response.body()!!)
//                        }

                    }else{
                        listener?.onFailedEnquiryGeneration()
                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getAllOngoingEnquiries(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getAllOngoingEnquiries(token)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryResponse> {
                override fun onFailure(call: Call<EnquiryResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Ongoing Enquiries","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryResponse>,
                    response: retrofit2.Response<EnquiryResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Ongoing Enquiries","Success: "+response.body()?.errorMessage)
                        EnquiryPredicates?.insertOngoingEnquiries(response?.body()!!)
                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("Ongoing Enquiries","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getAllCompletedEnquiries(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getAllCompletedEnquiries(token)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryResponse> {
                override fun onFailure(call: Call<EnquiryResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Completed Enquiries","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryResponse>,
                    response: retrofit2.Response<EnquiryResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Completed Enquiries","Success: "+response.body()?.errorMessage)
                        EnquiryPredicates?.insertCompletedEnquiries(response?.body()!!)
                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("Completed Enquiries","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getSingleOngoingEnquiry(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getSingleOngoingEnquiry(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryResponse> {
                override fun onFailure(call: Call<EnquiryResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Ongoing Enquiries","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryResponse>,
                    response: retrofit2.Response<EnquiryResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Enquiry Details","Success: "+response.body()?.errorMessage)
                        EnquiryPredicates?.insertOngoingEnquiries(response?.body()!!)
                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("Enquiry Details","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getSingleCompletedEnquiry(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getSingleCompletedEnquiry(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryResponse> {
                override fun onFailure(call: Call<EnquiryResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Enquiry Details","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryResponse>,
                    response: retrofit2.Response<EnquiryResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Enquiry Details","Success: "+response.body()?.errorMessage)
                        EnquiryPredicates?.insertCompletedEnquiries(response?.body()!!)
                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("Enquiry Details","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun markEnquiryCompleted(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .markEnquiryCompleted(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Mark Complete Enquiry","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>) {
                    Log.e("Mark Complete Enquiry","Success")

                    if(response?.isSuccessful){
                        //TODO : Remove Enquiry from ongoing enquiry
                        EnquiryPredicates.deleteEnquiry(enquiryId)
                    }
                }

            })
    }
}


