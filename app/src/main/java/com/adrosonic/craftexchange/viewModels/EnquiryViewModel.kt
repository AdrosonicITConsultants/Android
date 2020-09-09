package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.predicates.ClusterPredicates
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.OnGoingEnqResponse
import com.adrosonic.craftexchange.repository.data.response.moq.MoqDeliveryTimesResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class EnquiryViewModel(application: Application) : AndroidViewModel(application){
    companion object {
        const val TAG = "EnquiryViewModel"
    }

    interface GenerateEnquiryInterface{
        fun onSuccessEnquiryGeneration(enquiry : GenerateEnquiryResponse)
        fun onExistingEnquiryGeneration(productName : String, id : String, code : String)
        fun onFailedEnquiryGeneration()
    }

    interface FetchOngoingEnqInterface{
        fun onFailure()
        fun onSuccess()
    }
    interface MoqInterface{
        fun onAddMoqFailure()
        fun onAddMoqSuccess()
        fun onGetMoqCall()
    }
    val ongoingEnqList : MutableLiveData<RealmResults<OngoingEnquiries>> by lazy { MutableLiveData<RealmResults<OngoingEnquiries>>() }
    val enquiryDetails : MutableLiveData<OngoingEnquiries> by lazy { MutableLiveData<OngoingEnquiries>() }

    var listener: GenerateEnquiryInterface ?= null
    var moqListener: MoqInterface ?= null
    var fetchEnqListener : FetchOngoingEnqInterface ?= null

    fun getOnEnqListMutableData(): MutableLiveData<RealmResults<OngoingEnquiries>> {
        ongoingEnqList.value=loadOnEnqList()
        return ongoingEnqList
    }

    fun loadOnEnqList(): RealmResults<OngoingEnquiries>?{
        var ongoingEnqList = EnquiryPredicates.getAllOngoingEnquiries()
        Log.e("ongoingEnqList","ongoingEnqList :"+ongoingEnqList?.size)
        return ongoingEnqList
    }

    fun getSingleEnqMutableData(enqId : Long): MutableLiveData<OngoingEnquiries> {
        enquiryDetails.value=loadSingleEnqDetails(enqId)
        return enquiryDetails
    }

    fun loadSingleEnqDetails(enqId : Long): OngoingEnquiries?{
        var enquiryDetails = EnquiryPredicates.getSingleEnquiryDetails(enqId)
//        Log.e("enquiryDetails","enquiryDetails :"+ongoingEnqList?.size)
        return enquiryDetails
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
            .enqueue(object: Callback, retrofit2.Callback<OnGoingEnqResponse> {
                override fun onFailure(call: Call<OnGoingEnqResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Ongoing Enquiries","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<OnGoingEnqResponse>,
                    response: retrofit2.Response<OnGoingEnqResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Ongoing Enquiries","Success: "+response.body()?.errorMessage)
                        EnquiryPredicates?.insertBuyerOngoingEnquiries(response?.body()!!)
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

    fun getSingleEnquiry(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getSingleEnquiry(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<OnGoingEnqResponse> {
                override fun onFailure(call: Call<OnGoingEnqResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Ongoing Enquiries","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<OnGoingEnqResponse>,
                    response: retrofit2.Response<OnGoingEnqResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Enquiry Details","Success: "+response.body()?.errorMessage)
                        EnquiryPredicates?.insertBuyerOngoingEnquiries(response?.body()!!)
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
    fun getSingleMoq(enquiryId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"getSingleMoq :${enquiryId}")
        CraftExchangeRepository
            .getMoqService()
            .getMoq(token,enquiryId.toInt()).enqueue(object : Callback, retrofit2.Callback<SendMoqResponse> {
                override fun onFailure(call: Call<SendMoqResponse>, t: Throwable) {
                    Log.e(TAG,"getSingleMoq :${t.stackTrace}")
                    moqListener?.onGetMoqCall()
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<SendMoqResponse>,
                    response: Response<SendMoqResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"getSingleMoq :$valid")
                    if(valid){
                        response.body()?.data?.moq?.let {
                            MoqsPredicates.insertMoqs(response.body()?.data?.moq!!,response.body()?.data?.accepted?:false,enquiryId)
                        }
                    }
                    moqListener?.onGetMoqCall()
                }
            })
    }
    fun sendMoq(enquiryId:Long,additionalInfo: String,deliveryTimeID: Long,  moq: Long,ppu: String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        val request=SendMoqRequest(additionalInfo,deliveryTimeID,moq,ppu)
        CraftExchangeRepository
            .getMoqService()
            .sendMoq(token,enquiryId.toInt(),request).enqueue(object : Callback, retrofit2.Callback<SendMoqResponse> {
                override fun onFailure(call: Call<SendMoqResponse>, t: Throwable) {
                    Log.e(TAG,"sendMoq :${t.stackTrace}")
                    moqListener?.onAddMoqFailure()
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<SendMoqResponse>,
                    response: Response<SendMoqResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"sendMoq :${response.body()?.errorMessage}")
                    if(valid){
                        moqListener?.onAddMoqSuccess()
                        MoqsPredicates.insertMoqs(response.body()?.data?.moq!!,response.body()?.data?.accepted?:false,enquiryId)
                    }else moqListener?.onAddMoqFailure()
                }
            })
    }
}


