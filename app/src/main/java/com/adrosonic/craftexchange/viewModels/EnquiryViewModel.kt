package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.database.predicates.OrdersPredicates
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.DetailsData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.PayEnqInvResponse
import com.adrosonic.craftexchange.repository.data.response.marketing.ArtisanDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.GetMoqsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendSelectedMoqResponse
import com.adrosonic.craftexchange.repository.data.response.pi.SendPiResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.security.auth.callback.Callback

class EnquiryViewModel(application: Application) : AndroidViewModel(application){
    companion object {
        const val TAG = "EnquiryViewModel"
    }

    interface changeEnquiryInterface{
        fun onEnqChangeSuccess()
        fun onEnqChangeFailure()
    }

    interface GenerateEnquiryInterface{
        fun onSuccessEnquiryGeneration(enquiry : GenerateEnquiryResponse)
        fun onExistingEnquiryGeneration(productName : String, id : String, code : String)
        fun onFailedEnquiryGeneration()
    }
    interface FetchEnquiryInterface{
        fun onFailure()
        fun onSuccess()
    }

    interface MoqInterface{
        fun onAddMoqFailure()
        fun onAddMoqSuccess()
        fun onGetMoqCall()
    }
    interface BuyersMoqInterface{
        fun onGetMoqCall()
        fun onSendCustomMoqSuccess(moq:Long)
        fun onSendCustomMoqFailure()
    }
    interface ArtisanDetailsInterface{
        fun onFetch(data:ArtisanDetailsResponse?)
    }
    interface piInterface{
        fun onPiFailure()
        fun onPiSuccess()
        fun onPiDownloadSuccess()
        fun onPiDownloadFailure()
        fun onPiHTMLSuccess(data:String)
        fun onPiHTMLFailure()
    }

    interface singlePiInterface{
        fun onPiFailure()
        fun getPiSuccess(id : Long)
    }

    interface RevisePiInterface {
        fun onRevisePiFailure()
        fun onRevisePiSuccess()
    }

    interface PayEnqInvInterface{
        fun onFetchDetailsFailure()
        fun onFetchDetailsSuccess(details :DetailsData)
    }

    val ongoingEnqList : MutableLiveData<RealmResults<OngoingEnquiries>> by lazy { MutableLiveData<RealmResults<OngoingEnquiries>>() }
    val onGoEnqDetails : MutableLiveData<OngoingEnquiries> by lazy { MutableLiveData<OngoingEnquiries>() }

    val compEnqList : MutableLiveData<RealmResults<CompletedEnquiries>> by lazy { MutableLiveData<RealmResults<CompletedEnquiries>>() }
    val compEnqDetails : MutableLiveData<CompletedEnquiries> by lazy { MutableLiveData<CompletedEnquiries>() }

    //listeners
    var listener: GenerateEnquiryInterface ?= null
    var fetchEnqListener : FetchEnquiryInterface ?= null
    var moqListener: MoqInterface ?= null
    var buyerMoqListener : BuyersMoqInterface ?= null
    var artisanListener : ArtisanDetailsInterface ?= null
    var piLisener : piInterface ?= null
    var singlePiListener : singlePiInterface ?= null
    var changeEnqListener : changeEnquiryInterface ?= null
    var revisePiInterface : RevisePiInterface ?= null
    var payDetailsListener : PayEnqInvInterface ?= null

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
                        Log.e("EnquiryDetails","Success: "+response.body()?.errorMessage)
                        EnquiryPredicates?.insertOngoingEnquiries(response?.body()!!)
                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)

//                        Handler().postDelayed({
                            fetchEnqListener?.onSuccess()
//                        }, 5000)

                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("EnquiryDetails","Failure: "+response.body()?.errorMessage)
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
                            MoqsPredicates.insertMoq(response.body()?.data?.moq!!,response.body()?.data?.accepted?:false,enquiryId)
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
                    Log.e(TAG,"sendMoq :${response.body()?.valid}")
                    if(valid){
                        Log.e(TAG,"sendMoq ifff :${response.body()?.valid}")
                        moqListener?.onAddMoqSuccess()
                        MoqsPredicates.insertMoq(response.body()?.data?.moq!!,response.body()?.data?.accepted?:false,enquiryId)
                    }else moqListener?.onAddMoqFailure()
                }
            })
    }

    fun getMoqs(enquiryId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"getSingleMoq :${enquiryId}")
        CraftExchangeRepository
            .getMoqService()
            .getMoqs(token,enquiryId.toInt()).enqueue(object : Callback, retrofit2.Callback<GetMoqsResponse> {
                override fun onFailure(call: Call<GetMoqsResponse>, t: Throwable) {
                    Log.e(TAG,"getSingleMoq :${t.stackTrace}")
                    Log.e(TAG,"getSingleMoq :${t.message}")
                    Log.e(TAG,"getSingleMoq :${t.printStackTrace()}")
                    buyerMoqListener?.onGetMoqCall()
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<GetMoqsResponse>,
                    response: Response<GetMoqsResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"getSingleMoq :$valid")
                    if(valid){
                        Log.e(TAG,"getSingleMoq :${response.body()?.data?.size}")
                        response.body()?.data?.let {
                            MoqsPredicates.insertMoqs(response.body()?.data)
                        }
                    }
                    buyerMoqListener?.onGetMoqCall()
                }
            })
    }

    fun sendCustomMoqs(enquiryId:Long,moqId:Long,artisanId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"getSingleMoq :${enquiryId}")
        CraftExchangeRepository
            .getMoqService()
            .moqSelected(token,enquiryId.toInt(),moqId.toInt(),artisanId.toInt()).enqueue(object : Callback, retrofit2.Callback<SendSelectedMoqResponse> {
                override fun onFailure(call: Call<SendSelectedMoqResponse>, t: Throwable) {
                    Log.e(TAG,"getSingleMoq :${t.message}")
                    t.printStackTrace()
                    buyerMoqListener?.onSendCustomMoqFailure()
                }
                override fun onResponse(
                    call: Call<SendSelectedMoqResponse>,
                    response: Response<SendSelectedMoqResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"getSingleMoq :$valid")
                    if(valid){
                        Log.e(TAG,"getSingleMoq :${response.body()?.data}")
                        response.body()?.data?.let {
                            MoqsPredicates.deleteMoqs(enquiryId,moqId)
//                            MoqsPredicates.updateMoqsPostSelection(response.body()?.data,enquiryId)
                        }
                        buyerMoqListener?.onSendCustomMoqSuccess(moqId)
                    }else buyerMoqListener?.onSendCustomMoqFailure()
                }
            })
    }

    fun getArtisanProfile(artisanId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"getArtisanProfile :${artisanId}")
        CraftExchangeRepository
            .getMarketingService()
            .getArtisanDao(token,artisanId).enqueue(object : Callback, retrofit2.Callback<ArtisanDetailsResponse> {
                override fun onFailure(call: Call<ArtisanDetailsResponse>, t: Throwable) {
                    Log.e(TAG,"getArtisanProfile :${t.message}")
                    t.printStackTrace()
                    artisanListener?.onFetch(null)
                }
                override fun onResponse(
                    call: Call<ArtisanDetailsResponse>,
                    response: Response<ArtisanDetailsResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"getArtisanProfile :$valid")
                    if(valid){
                        Log.e(TAG,"getArtisanProfile :${response.body()?.data}")
                        artisanListener?.onFetch(response.body())
                    }
                    else  artisanListener?.onFetch(null)
                }
            })
    }

    fun savePi(enquiryId:Long,_id:Long,pi: SendPiRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"savePi :${enquiryId}")
        Log.e(TAG,"pi :${Gson().toJson(pi)}")
        CraftExchangeRepository
            .getPiService()
            .savePI(token,enquiryId.toInt(),pi).enqueue(object : Callback, retrofit2.Callback<SendPiResponse> {
                override fun onFailure(call: Call<SendPiResponse>, t: Throwable) {
                    Log.e(TAG,"savePi :${t.message}")
                    t.printStackTrace()
                    piLisener?.onPiFailure()
                }
                override fun onResponse(
                    call: Call<SendPiResponse>,
                    response: Response<SendPiResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"savePi :$valid")
                    if(valid){
                        Log.e(TAG,"savePi :${response.body()?.data}")
                        response.body()?.data?.let {
                            piLisener?.onPiSuccess()
                        }
                    } else piLisener?.onPiFailure()
                }
            })
    }

    fun sendPi(enquiryId:Long,pi: SendPiRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"sendPi :${enquiryId}")
        Log.e(TAG,"sendPi pi :${Gson().toJson(pi)}")
        CraftExchangeRepository
            .getPiService()
            .sendPI(token,enquiryId.toInt(),pi).enqueue(object : Callback, retrofit2.Callback<SendPiResponse> {
                override fun onFailure(call: Call<SendPiResponse>, t: Throwable) {
                    Log.e(TAG,"sendPi :${t.message}")
                    t.printStackTrace()
                    piLisener?.onPiFailure()
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
                            piLisener?.onPiSuccess()
                        }
                    } else piLisener?.onPiFailure()
                }
            })
    }

    fun downloadPi(enquiryId:Long,isOld:String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"downloadPi :${enquiryId}")
        CraftExchangeRepository
            .getPiService()
            .getPreviewPiPDF(token,enquiryId.toInt(),isOld).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG,"downloadPi :${t.message}")
                    t.printStackTrace()
                    piLisener?.onPiDownloadFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val body=response.body()
                    if(body!=null) {
                        body?.let {
                            Utility.writeResponseBodyToDisk(
                                it,
                                enquiryId.toString(),
                                if(isOld.equals("true")) "Old"  else "",
                                getApplication()
                            )
                            Timer().schedule(object : TimerTask() {
                                override fun run() {
                                    piLisener?.onPiDownloadSuccess()
                                }
                            }, 500)

                        }
                    }else  piLisener?.onPiDownloadFailure()
                }
            })
    }

    fun previewPi(enquiryId:Long,isOld:String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"previewPi :${enquiryId}")
        CraftExchangeRepository
            .getPiService()
            .getPreviewPiHTML(token,enquiryId.toInt(),isOld).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG,"previewPi :${t.message}")
                    t.printStackTrace()
                    piLisener?.onPiHTMLFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val body=response.body()
                    if(body!=null) {
                        Log.e(TAG,"previewPi :${body}")
                        body?.let {
                            piLisener?.onPiHTMLSuccess(it.string())
                        }
                    }else  {
                        Log.e(TAG,"previewPi :${body}")
                        piLisener?.onPiHTMLFailure()
                    }
                }
            })
    }

    fun getSinglePi(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"getSinglePi :${enquiryId}")
        CraftExchangeRepository
            .getPiService()
            .getSinglePi(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<SendPiResponse> {
                override fun onFailure(call: Call<SendPiResponse>, t: Throwable) {
                    Log.e(TAG,"getSinglePi :${t.message}")
                    t.printStackTrace()
                    singlePiListener?.onPiFailure()
                }
                override fun onResponse(
                    call: Call<SendPiResponse>,
                    response: Response<SendPiResponse>
                ) {
                    response?.body()?.data?.id?.let {
                        PiPredicates.insertPi(response?.body()!!)
                        singlePiListener?.getPiSuccess(it)
                    }
                }
            })
    }

    fun setEnquiryStage(enquiryId: Long, enqStageId: Long, innerEnqStageId: Long?){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .setEnquiryStages(token,enqStageId,enquiryId, innerEnqStageId!!)
            .enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    changeEnqListener?.onEnqChangeFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response?.isSuccessful){
                        changeEnqListener?.onEnqChangeSuccess()
                    }else{
                        changeEnqListener?.onEnqChangeFailure()
                    }
                }
            })
    }

    fun setCompleteOrderStage(enquiryId: Long, enqStageId: Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .setCompleteOrderStage(token,enqStageId,enquiryId)
            .enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    changeEnqListener?.onEnqChangeFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response?.isSuccessful){
                        changeEnqListener?.onEnqChangeSuccess()
                    }else{
                        changeEnqListener?.onEnqChangeFailure()
                    }
                }
            })
    }

    fun revisePi(enquiryId:Long,pi: SendPiRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"revisePi :${enquiryId}")
        Log.e(TAG,"revisePi pi :${Gson().toJson(pi)}")
        CraftExchangeRepository
            .getPiService()
            .revisedPI(token,enquiryId.toInt(),pi).enqueue(object : Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    Log.e(TAG,"onFailure revisePi :${t.message}")
                    t.printStackTrace()
                    revisePiInterface?.onRevisePiFailure()
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: Response<NotificationReadResponse>
                ) {
                    val valid=response.body()?.valid?:false
                    Log.e(TAG,"onResponse revisePi :$valid")
                    if(valid){
                        Log.e(TAG,"onResponse true")
                        OrdersPredicates.updatIsPiSend(enquiryId,1L)
                            revisePiInterface?.onRevisePiSuccess()
                    } else revisePiInterface?.onRevisePiFailure()
                }
            })
    }

    fun getOldPiData(enquiryId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"getOldPiData :${enquiryId}")
        CraftExchangeRepository
            .getPiService()
            .getOldPiData(token,enquiryId.toInt()).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG,"getOldPiData :${t.message}")
                    t.printStackTrace()
                    piLisener?.onPiHTMLFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.e(TAG,"onResponse getOldPiData :${response.code()}")
                    piLisener?.onPiHTMLFailure()
                  if(response.code()==200) {
                      OrdersPredicates.updatIsPiSend(enquiryId,1L)
                      EnquiryPredicates.updatePiStatus(enquiryId,1L)
                  }
                   else {
                      OrdersPredicates.updatIsPiSend(enquiryId,0L)
                      EnquiryPredicates.updatePiStatus(enquiryId,0L)
                  }
                }
            })
    }

    fun fetchPayEnqInvDetails(enquiryId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getPayEnqInvDetails(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<PayEnqInvResponse> {
                override fun onFailure(call: Call<PayEnqInvResponse>, t: Throwable) {
                    t.printStackTrace()
                    payDetailsListener?.onFetchDetailsFailure()
                }
                override fun onResponse(
                    call: Call<PayEnqInvResponse>,
                    response: Response<PayEnqInvResponse>
                ) {
                    if(response.body()?.valid == true){
                        payDetailsListener?.onFetchDetailsSuccess(response?.body()?.data!!)
                    }else{
                        payDetailsListener?.onFetchDetailsFailure()
                    }
                }
            })
    }

}


