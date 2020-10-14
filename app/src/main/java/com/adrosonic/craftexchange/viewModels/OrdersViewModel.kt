package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.changeRequest.RaiseCrInput
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.marketing.ArtisanDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.GetMoqsResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchange.repository.data.response.moq.SendSelectedMoqResponse
import com.adrosonic.craftexchange.repository.data.response.orders.OrderResponse
import com.adrosonic.craftexchange.repository.data.response.pi.SendPiResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.io.File
import javax.security.auth.callback.Callback

class OrdersViewModel(application: Application) : AndroidViewModel(application){
    companion object {
        const val TAG = "OrdersViewModel"
    }

    interface FetchOrderInterface{
        fun onFailure()
        fun onSuccess()
    }
    interface changeStatusInterface{
        fun onStatusChangeSuccess()
        fun onStatusChangeFailure()
    }
    interface ToggleChangeInterface{
        fun onToggleSuccess()
        fun onToggleFailure()
    }
    interface FetchCrInterface{
        fun onFetchCrSuccess()
        fun onFetchCrFailure()
    }
    val ongoingOrderList : MutableLiveData<RealmResults<Orders>> by lazy { MutableLiveData<RealmResults<Orders>>() }
    val onGoingOrderDetails : MutableLiveData<Orders> by lazy { MutableLiveData<Orders>() }

    val compOrderList : MutableLiveData<RealmResults<Orders>> by lazy { MutableLiveData<RealmResults<Orders>>() }

    var fetchEnqListener : FetchOrderInterface ?= null
    var changeStatusListener : changeStatusInterface?= null
    var toggleListener : ToggleChangeInterface?= null
    var fetcCrListener : FetchCrInterface?= null

    fun getOnOrderListMutableData(): MutableLiveData<RealmResults<Orders>> {
        ongoingOrderList.value=loadOnOrderList()
        return ongoingOrderList
    }

    fun loadOnOrderList(): RealmResults<Orders>?{
        var ongoingOrderList = OrdersPredicates.getAllOngoingOrders()
        Log.e("ongoingOrderList","ongoingOrderList :"+ongoingOrderList?.size)
        return ongoingOrderList
    }

    fun getCompOrderListMutableData(): MutableLiveData<RealmResults<Orders>> {
        compOrderList.value=loadCompOrderList()
        return compOrderList
    }

    fun loadCompOrderList(): RealmResults<Orders>?{
        var compOrderList = OrdersPredicates.getAllCompletedOrders()
        Log.e("compOrderList","compOrderList :"+compOrderList?.size)
        return compOrderList!!
    }
    fun getSingleOnOrderData(enqId : Long,isCompleted:Long): MutableLiveData<Orders> {
        onGoingOrderDetails.value=loadSingleOrderDetails(enqId,isCompleted)
        return onGoingOrderDetails
    }

    fun loadSingleOrderDetails(enqId : Long,isCompleted:Long): Orders?{
        var orderDetails = OrdersPredicates.getSingleOnGoOrderDetails(enqId,isCompleted)
        return orderDetails
    }


    fun getAllOngoingOrders(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .getAllOpenOrders(token)
            .enqueue(object: Callback, retrofit2.Callback<OrderResponse> {
                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Ongoing Enquiries","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: retrofit2.Response<OrderResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Ongoing Enquiries","Success: "+response.body()?.errorMessage)
                        OrdersPredicates.insertOngoingOrders(response?.body(),0)
//                        EnquiryPredicates?.insertOngoingEnquiries(response?.body()!!)
//                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("Ongoing Enquiries","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getAllCompletedOrders(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .getAllClosedOrders(token)
            .enqueue(object: Callback, retrofit2.Callback<OrderResponse> {
                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Completed Enquiries","Failure: "+t.message)
                }

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: retrofit2.Response<OrderResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Completed Enquiries","Success: "+response.body()?.errorMessage)
                        OrdersPredicates.insertOngoingOrders(response?.body(),1)
//                        EnquiryPredicates?.insertCompletedEnquiries(response?.body()!!)
//                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("Completed Enquiries","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getSingleOngoingOrder(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("OrderDetails","getSingleOngoingOrder: "+enquiryId)
        CraftExchangeRepository
            .getOrderService()
            .getSingleOngoingOrder(token,enquiryId.toInt())
            .enqueue(object: Callback, retrofit2.Callback<OrderResponse> {
                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("OrderDetails","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: retrofit2.Response<OrderResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("OrderDetails","Success: "+response.body()?.data?.size)
                        OrdersPredicates?.insertOngoingOrders(response?.body()!!,0)
//                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                fetchEnqListener?.onSuccess()
                            }
                        }, 100)
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("OrderDetails","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getSingleCompletedOrder(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("OrderDetails","getCompletedOrder: $enquiryId")
        CraftExchangeRepository
            .getOrderService()
            .getSingleClosedOrder(token,enquiryId.toInt())
            .enqueue(object: Callback, retrofit2.Callback<OrderResponse> {
                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("OrderDetails","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: retrofit2.Response<OrderResponse>) {
                    if(response.body()?.valid!!){
                        Log.e("OrderDetails","Success: "+response.body()?.data?.size)
                        OrdersPredicates?.insertOngoingOrders(response?.body()!!,1)
//                        OrdersPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        OrdersPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("OrderDetails","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun setEnquiryStage(enquiryId: Long, enqStageId: Long, innerEnqStageId: Long?){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("OrderDetails","setEnquiryStage: "+enquiryId)
        Log.e("OrderDetails","enqStageId: "+enqStageId)
        CraftExchangeRepository
            .getEnquiryService()
            .setEnquiryStages(token,enqStageId,enquiryId, innerEnqStageId!!)
            .enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    changeStatusListener?.onStatusChangeFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response?.isSuccessful){
                        Log.e("OrderDetails","isSuccessful true")
                        changeStatusListener?.onStatusChangeSuccess()
                    }else{
                        Log.e("OrderDetails","isSuccessful false")
                        changeStatusListener?.onStatusChangeFailure()
                    }
                }
            })
    }

    fun setCompleteOrderStage(enquiryId: Long, enqStageId: Long){
        Log.e("OrderDetails","setCompleteOrderStage: "+enquiryId)
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .setCompleteOrderStage(token,enqStageId,enquiryId)
            .enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("OrderDetails","isSuccessful false")
                    changeStatusListener?.onStatusChangeFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response?.isSuccessful){
                        Log.e("OrderDetails","isSuccessful ")
                        changeStatusListener?.onStatusChangeSuccess()
                    }else{
                        Log.e("OrderDetails","isSuccessful false")
                        changeStatusListener?.onStatusChangeFailure()
                    }
                }
            })
    }

    fun setCrToggle(enquiryId: Long){
        Log.e("Toggle","enquiryId: "+enquiryId)
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getCrService()
            .toggleChangeRequestFromArtisan(token,enquiryId.toInt(),0)
            .enqueue(object : Callback, retrofit2.Callback<DeleteOwnProductRespons> {
                override fun onFailure(call: Call<DeleteOwnProductRespons>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Toggle","isSuccessful false")
                    toggleListener?.onToggleFailure()
                }
                override fun onResponse(
                    call: Call<DeleteOwnProductRespons>,
                    response: Response<DeleteOwnProductRespons>
                ) {
                    Log.e("Toggle","onResponse : ${response?.body()?.data}")
                    if(response?.body()?.valid!!){
                        Log.e("Toggle","isSuccessful ")
                        OrdersPredicates.updateCrStatus(enquiryId)
                        toggleListener?.onToggleSuccess()
                    }else{
                        Log.e("Toggle","isSuccessful false")
                        toggleListener?.onToggleFailure()
                    }
                }
            })
    }

    fun getChangeRequestDetails(enquiryId: Long){
        Log.e("CrDetails","enquiryId: "+enquiryId)
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getCrService()
            .getChangeRequestDetails(token,enquiryId.toInt())
            .enqueue(object : Callback, retrofit2.Callback<CrDetailsResponse> {
                override fun onFailure(call: Call<CrDetailsResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("CrDetails","isSuccessful false")
                    fetcCrListener?.onFetchCrFailure()
                }
                override fun onResponse(
                    call: Call<CrDetailsResponse>,
                    response: Response<CrDetailsResponse>
                ) {
                    Log.e("CrDetails","onResponse : ${response?.body()?.data}")
                    if(response?.body()?.valid!!){
                        Log.e("CrDetails","isSuccessful ")
                        if(response?.body()!!.data!!.changeRequestItemList!!.size>0){
                            CrPredicates.insertChangeReq(response?.body())
                            fetcCrListener?.onFetchCrSuccess()
                        }else  fetcCrListener?.onFetchCrFailure()
                    }else{
                        Log.e("CrDetails","isSuccessful false")
                        fetcCrListener?.onFetchCrFailure()
                    }
                }
            })
    }

    fun raiseChangeRequest(enquiryId: Long,changeRequestParameters : RaiseCrInput){
        Log.e("RaiseCr","enquiryId: "+enquiryId)
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getCrService()
            .raiseChangeRequest(token,enquiryId.toInt(),changeRequestParameters)
            .enqueue(object : Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("RaiseCr","isSuccessful false")
                    fetcCrListener?.onFetchCrFailure()
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: Response<NotificationReadResponse>
                ) {
                    Log.e("RaiseCr","onResponse : ${response?.body()?.data}")
                    if(response?.body()?.valid!!){
                        Log.e("RaiseCr","isSuccessful ")
                        OrdersPredicates.updateChangerequestStatus(enquiryId)
                        fetcCrListener?.onFetchCrSuccess()
                    }else{
                        Log.e("RaiseCr","isSuccessful false")
                        fetcCrListener?.onFetchCrFailure()
                    }
                }
            })
    }
}


