package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.OrderProgressDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.changeRequest.RaiseCrInput
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiPreviewRequest
import com.adrosonic.craftexchange.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.Rating.RatingEnquiryUserResponse
import com.adrosonic.craftexchange.repository.data.response.Rating.RatingQuestionsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.orders.OrderProgressResponse
import com.adrosonic.craftexchange.repository.data.response.orders.OrderResponse
import com.adrosonic.craftexchange.repository.data.response.taxInv.TaxInvPreviewResponse
import com.adrosonic.craftexchange.repository.data.response.taxInv.TaxInvoiceResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.security.auth.callback.Callback

class OrdersViewModel(application: Application) : AndroidViewModel(application){
    companion object {
        const val TAG = "OrdersViewModel"
    }

    interface GetOrderProgressInterface{
        fun onOPFailure()
        fun onOPSuccess()
    }

    interface RecreationDispatchInterface{
        fun onRDFailure()
        fun onRDSuccess()
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
    interface UpdateCrStatusInterface{
        fun onCrStatusSuccess(changeRequestStatus:Long)
        fun onCrStatusFailure()
    }
    interface OrderCinfirmedInterface{
        fun onSuccess()
        fun onFailure()
    }
    interface GenTaxInvInterface{
        fun onGenTaxInvSuccess()
        fun onGenTaxInvFailure()
    }

    interface RatingDataInteface{
        fun onRatingSuccess(string: String)
        fun onRatingFailure()
    }


    interface OrderCloseInterface{
        fun onOrderCloseSuccess()
        fun onOrderCloseFailure()
    }

    interface tiInterface{
        //        fun onTiFailure()
//        fun onTiSuccess()
        fun onTiDownloadSuccess()
        fun onTiDownloadFailure()
        fun onTiHTMLSuccess(data:String)
        fun onTiHTMLFailure()
    }
    val ongoingOrderList : MutableLiveData<RealmResults<Orders>> by lazy { MutableLiveData<RealmResults<Orders>>() }
    val onGoingOrderDetails : MutableLiveData<Orders> by lazy { MutableLiveData<Orders>() }

    val compOrderList : MutableLiveData<RealmResults<Orders>> by lazy { MutableLiveData<RealmResults<Orders>>() }
    var ratingListener : RatingDataInteface ?= null

    var recreationDispatchListener : RecreationDispatchInterface ?= null
    var getOrderProgressListener : GetOrderProgressInterface ?= null
    var fetchEnqListener : FetchOrderInterface ?= null
    var changeStatusListener : changeStatusInterface?= null
    var toggleListener : ToggleChangeInterface?= null
    var fetcCrListener : FetchCrInterface?= null
    var updateCrListener : UpdateCrStatusInterface?= null
    var orderConfirmListener : OrderCinfirmedInterface?= null
    var taxInvGenListener : GenTaxInvInterface?=null
    var tiListener : tiInterface?=null
    var orderCloseListener : OrderCloseInterface?=null

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

    fun loadOrderProgressDetails(enqId : Long): OrderProgressDetails?{
        var orderDetails = OrdersPredicates.getOrderProgressDetails(enqId)
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
                        OrdersPredicates?.insertOrdPaymentDetails(response?.body()!!)
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
                        OrdersPredicates.updateChangeRequestStatus(enquiryId,0L)
                        fetcCrListener?.onFetchCrSuccess()
                    }else{
                        Log.e("RaiseCr","isSuccessful false")
                        fetcCrListener?.onFetchCrFailure()
                    }
                }
            })
    }

    fun acceptRejectChangeRequest(changeRequestParameters : RaiseCrInput,changeRequestStatus:Long){
        Log.e("RaiseCr","changeRequestStatus : $changeRequestStatus ")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getCrService()
            .changeRequestStatusUpdate(token,changeRequestParameters,changeRequestStatus.toInt())
            .enqueue(object : Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("RaiseCr","isSuccessful false")
                    updateCrListener?.onCrStatusFailure()
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: Response<NotificationReadResponse>
                ) {
                    Log.e("RaiseCr","onResponse : ${response?.body()?.data}")
                    if(response?.body()?.valid!!){
                        Log.e("RaiseCr","isSuccessful ")
                        CrPredicates.updatePostCrStatus(changeRequestParameters,changeRequestStatus)
                        OrdersPredicates.updateChangeRequestStatus(changeRequestParameters.enquiryId,changeRequestStatus)

                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                updateCrListener?.onCrStatusSuccess(changeRequestStatus)
                            }
                        }, 1100)
                    }else{
                        Log.e("RaiseCr","isSuccessful false")
                        updateCrListener?.onCrStatusFailure()
                    }
                }
            })
    }

    fun markOrderAsReceived(orderId:Long,orderReceivedDate:String){
        Log.e("markOrderAsReceived","orderId : $orderId ")
        Log.e("markOrderAsReceived","orderReceivedDate : $orderReceivedDate ")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .markOrderAsReceived(token,orderId.toInt(),orderReceivedDate,1)
            .enqueue(object : Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("markOrderAsReceived","isSuccessful false")
                    orderConfirmListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: Response<NotificationReadResponse>
                ) {
                    Log.e("markOrderAsReceived","onResponse : ${response?.body()?.data}")
                    if(response?.body()?.valid!!){
                        markEnquiryCompleted(orderId)
                    }else{
                        Log.e("markOrderAsReceived","isSuccessful false")
                        orderConfirmListener?.onFailure()
                    }
                }
            })
    }

    fun markEnquiryCompleted(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .markEnquiryCompleted(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Mark Complete Enquiry","Failure: "+t.message)
                    orderConfirmListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: retrofit2.Response<NotificationReadResponse>) {
                    Log.e("Mark Complete Enquiry","Success")

                    if(response?.body()?.valid!!){
                        OrdersPredicates.updatPostDeliveryConfirmed(enquiryId)
                        EnquiryPredicates.deleteEnquiry(enquiryId)
                        orderConfirmListener?.onSuccess()
                    }else{
                        Log.e("markOrderAsReceived","isSuccessful false")
                        orderConfirmListener?.onFailure()
                    }
                }

            })
    }

    fun recreateOrder(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .recreateOrder(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    recreationDispatchListener?.onRDFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>) {
                    if(response?.isSuccessful){
                        recreationDispatchListener?.onRDSuccess()
                    }
                }
            })
    }

    fun markOrderDispatchedAfterRecreation(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .orderDispatchAfterRecreation(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    recreationDispatchListener?.onRDFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>) {
                    if(response?.isSuccessful){
                        recreationDispatchListener?.onRDSuccess()
                    }
                }
            })
    }


    fun initializePartialRefund(enquiryId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .initializePartialRefund(token,enquiryId)
            .enqueue(object: Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("initializePartialRefund","Failure: "+t.message)
                    orderCloseListener?.onOrderCloseFailure()
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: retrofit2.Response<NotificationReadResponse>) {
                    Log.e("initializePartialRefund","Success")

                    if(response?.body()?.valid!!){
                        Log.e("initializePartialRefund","Success: ${response?.body()?.valid}")
                        OrdersPredicates.updatPostInitializePartialRefund(enquiryId)
                        orderCloseListener?.onOrderCloseSuccess()

                    }else{
                        Log.e("initializePartialRefund","isSuccessful false")
                        orderCloseListener?.onOrderCloseFailure()
                    }
                }

            })
    }

    fun generateTaxInvoice(invoiceRequest : SendTiRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getTiService()
            .generateTaxInvoice(token,invoiceRequest)
            .enqueue(object : Callback, retrofit2.Callback<TaxInvoiceResponse> {
                override fun onFailure(call: Call<TaxInvoiceResponse>, t: Throwable) {
                    t.printStackTrace()
                    taxInvGenListener?.onGenTaxInvFailure()
                    Log.e("TaxInvoice","onFailure ${t.message}")
                }
                override fun onResponse(
                    call: Call<TaxInvoiceResponse>,
                    response: Response<TaxInvoiceResponse>
                ) {
                    Log.e("TaxInvoice","111 ${response?.body()?.valid}")
                    Log.e("TaxInvoice","222 ${response?.body()?.data?.id}")
                    if(response?.body()?.valid!!) {
                        taxInvGenListener?.onGenTaxInvSuccess()
                        TaxInvPredicates.insertTi(response.body()!!)
                    }else{
                        taxInvGenListener?.onGenTaxInvFailure()
                    }
                }
            })
    }

    fun generateTaxInvoicePreview(invoiceRequest : SendTiPreviewRequest){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getTiService()
            .generateTaxInvoicePreview(token,invoiceRequest)
            .enqueue(object : Callback, retrofit2.Callback<TaxInvPreviewResponse> {
                override fun onFailure(call: Call<TaxInvPreviewResponse>, t: Throwable) {
                    t.printStackTrace()
                    taxInvGenListener?.onGenTaxInvFailure()
                }
                override fun onResponse(
                    call: Call<TaxInvPreviewResponse>,
                    response: Response<TaxInvPreviewResponse>
                ) {
                    if(response?.body()?.valid == true){
                        taxInvGenListener?.onGenTaxInvSuccess()
                        Prefs.putString(ConstantsDirectory.TAX_INV_WEB_STRING,response?.body()?.data)
                    }else{
                        taxInvGenListener?.onGenTaxInvFailure()
                    }
                }
            })
    }


    fun downloadTi(enquiryId:Long){
        //TODO : API not fixed
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(EnquiryViewModel.TAG,"downloadTi :${enquiryId}")
        CraftExchangeRepository
            .getTiService()
            .getPreviewTaxInvPDF(token,enquiryId.toInt(),"false").enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(EnquiryViewModel.TAG,"downloadTi :${t.message}")
                    t.printStackTrace()
                    tiListener?.onTiDownloadFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val body=response.body()
                    if(body!=null) {
                        body?.let {
                            Utility.writeTIResponseBodyToDisk(
                                it,
                                enquiryId.toString(),
                                "",
                                getApplication()
                            )
                            Timer().schedule(object : TimerTask() {
                                override fun run() {
                                    tiListener?.onTiDownloadSuccess()
                                }
                            }, 500)

                        }
                    }else  tiListener?.onTiDownloadFailure()
                }
            })
    }

    fun previewTi(enquiryId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(EnquiryViewModel.TAG,"previewTi :${enquiryId}")
        CraftExchangeRepository
            .getTiService()
            .getPreviewTaxInvHTML(token,enquiryId.toInt(),"false").enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(EnquiryViewModel.TAG,"previewTi :${t.message}")
                    t.printStackTrace()
                    tiListener?.onTiHTMLFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val body=response.body()
                    if(body!=null) {
                        Log.e(EnquiryViewModel.TAG,"previewTi :${body}")
                        body?.let {
                            tiListener?.onTiHTMLSuccess(it.string())
                        }
                    }else  {
                        Log.e(EnquiryViewModel.TAG,"previewTi :${body}")
                        tiListener?.onTiHTMLFailure()
                    }
                }
            })
    }

    fun ratingData(enquiryId: Long , userId :Long){
        Log.d("RatingAPI", "function called: $enquiryId $userId")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getUserEnquiryRatingservise()
            .getRatingEnquiryUser(
                token,
                enquiryId,
                userId
            ).enqueue(object : Callback, retrofit2.Callback<RatingEnquiryUserResponse> {
                override fun onFailure(call: Call<RatingEnquiryUserResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d("RatingAPI","no response ")

                }
                override fun onResponse(
                    call: Call<RatingEnquiryUserResponse>,
                    response: Response<RatingEnquiryUserResponse>) {
                    if(response.body()?.valid == true){

                        val body= Gson().toJson(response.body())
                        Log.d("RatingAPI","response: "+body)

                        ratingListener?.onRatingSuccess(body)
                        UserConfig.shared.showBuyerRating= Gson().toJson(response.body())


                    }else{
//                        fetchEnqListener?.onFailure()
                        Log.d("RatingAPI","Failure: "+response.body()?.errorMessage)
                    }
                }
            })
        }

    fun ratingQuestions(){
        Log.d("RatingAPI", "function called:ratingQuestions")

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getUserEnquiryRatingservise()
            .getRatingQuestions(
                token
            ).enqueue(object : Callback, retrofit2.Callback<RatingQuestionsResponse> {
                override fun onFailure(call: Call<RatingQuestionsResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d("RatingAPI","no response ")

                }
                override fun onResponse(
                    call: Call<RatingQuestionsResponse>,
                    response: Response<RatingQuestionsResponse>) {
//                    Log.d("RatingAPI","hhh")
                    if(response.body()?.valid == true){
//                        val body = response.body()

//                        Log.d("RatingAPI","response: "+body)
////                        val body= Gson().toJson(response.body())
//                        Log.d("RatingAPI","response: "+body)
                        UserConfig.shared.ratingQuestions= Gson().toJson(response.body())

//                        ratingListener?.onRatingSuccess(body)

                    }else{
//                        fetchEnqListener?.onFailure()
                        Log.d("RatingAPI","Failure: "+response.body()?.errorMessage)
                    }
                }
            })
    }

    fun getOrderProgressDetails(orderId: Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getOrderService()
            .getOrderProgress(token,orderId)
            .enqueue(object: Callback, retrofit2.Callback<OrderProgressResponse> {
                override fun onFailure(call: Call<OrderProgressResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("Order Progress","Failure: "+t.message)
                    getOrderProgressListener?.onOPFailure()
                }
                override fun onResponse(
                    call: Call<OrderProgressResponse>,
                    response: retrofit2.Response<OrderProgressResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("Order Progress","Success: "+response.body()?.errorMessage)
                        getOrderProgressListener?.onOPSuccess()
                        OrdersPredicates?.insertOrderProgressDetails(response?.body())
                    }else{
                        Log.e("Order Progress","Failure: "+response.body()?.errorMessage)
                        getOrderProgressListener?.onOPFailure()
                    }
                }

            })
    }
}



