package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.database.predicates.MoqsPredicates
import com.adrosonic.craftexchange.database.predicates.OrdersPredicates
import com.adrosonic.craftexchange.database.predicates.PiPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
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

    val ongoingOrderList : MutableLiveData<RealmResults<Orders>> by lazy { MutableLiveData<RealmResults<Orders>>() }
    val onGoingOrderDetails : MutableLiveData<Orders> by lazy { MutableLiveData<Orders>() }

    val compOrderList : MutableLiveData<RealmResults<Orders>> by lazy { MutableLiveData<RealmResults<Orders>>() }
    val compOrderDetails : MutableLiveData<Orders> by lazy { MutableLiveData<Orders>() }

    var fetchEnqListener : FetchOrderInterface ?= null

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
        CraftExchangeRepository
            .getOrderService()
            .getSingleOngoingOrder(token,enquiryId)
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
                        Log.e("Order Details","Success: "+response.body()?.errorMessage)
                        OrdersPredicates?.insertOngoingOrders(response?.body()!!,0)
//                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("Order Details","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getSingleCompletedOrder(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .getSingleClosedOrder(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<OrderResponse> {
                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFailure()
                    Log.e("Order Details","Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: retrofit2.Response<OrderResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Order Details","Success: "+response.body()?.errorMessage)
                        OrdersPredicates?.insertOngoingOrders(response?.body()!!,1)
//                        OrdersPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        OrdersPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onSuccess()
                    }else{
                        fetchEnqListener?.onFailure()
                        Log.e("Order Details","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }


}


