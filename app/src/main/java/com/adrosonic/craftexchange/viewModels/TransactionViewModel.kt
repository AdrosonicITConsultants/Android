package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.Transactions
import com.adrosonic.craftexchange.database.predicates.TransactionPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.repository.data.response.enquiry.payment.PaymentReceiptResponse
import com.adrosonic.craftexchange.repository.data.response.taxInv.FinPayData
import com.adrosonic.craftexchange.repository.data.response.taxInv.FinalPayDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.AdvancePaymentStatus
import com.adrosonic.craftexchange.repository.data.response.transaction.AdvancePaymentStatusResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.SingleTransactionResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.TransactionResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import javax.security.auth.callback.Callback

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "TransactionViewModel"
    }

    interface UploadPaymentInterface{
        fun onFailure()
        fun onSuccess()
    }

    interface ValidatePaymentInterface{
        fun onPaymentFailure()
        fun onPaymentSuccess()
    }

    interface PaymentReceiptInterface{
        fun onFailure()
        fun onSuccess(imgName : String, receiptId : Long)
    }

    interface FinalPayDetailsInterface{
        fun onFPDFailure()
        fun onFPDSuccess(details : FinPayData)
    }

    interface TransactionInterface{
        fun onGetTransactionsSuccess()
        fun onGetTransactionsFailure()
    }

    interface AdvPayStatusInterface{
        fun onAdvPayFetchSuccess(data: AdvancePaymentStatus)
        fun onAdvPayFetchFailure()
    }

    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

    var uploadPaymentListener : UploadPaymentInterface ?= null
    var validatePaymentListener : ValidatePaymentInterface ?= null
    var paymentReceiptListener : PaymentReceiptInterface ?= null
    var transactionListener : TransactionInterface ?= null
    var finalPayDetailsListener : FinalPayDetailsInterface?=null
    var advPayListener : AdvPayStatusInterface?=null


    val ongoingTranList : MutableLiveData<RealmResults<Transactions>> by lazy { MutableLiveData<RealmResults<Transactions>>() }
    val completedTranList : MutableLiveData<RealmResults<Transactions>> by lazy { MutableLiveData<RealmResults<Transactions>>() }


    fun getOnTranListMutableData(): MutableLiveData<RealmResults<Transactions>> {
        ongoingTranList.value=loadOnTranList()
        return ongoingTranList
    }

    fun loadOnTranList(): RealmResults<Transactions>?{
        var ongoingTranList = TransactionPredicates.getAllOngoingTransactions()
        return ongoingTranList
    }

    fun getCompTranListMutableData(): MutableLiveData<RealmResults<Transactions>> {
        completedTranList.value=loadCompTranList()
        return completedTranList
    }

    fun loadCompTranList(): RealmResults<Transactions>?{
        var completedTranList = TransactionPredicates.getAllCompletedTransactions()
        return completedTranList
    }

    fun uploadPaymentReceipt(payObj : BuyerPayment, filePath : String) {

        var gson = Gson()
        var payObjString = gson.toJson(payObj)
        Log.e("uploadPaymentReceipt","$payObjString")
        var file = File(filePath)
        var fileReqBody = file!!.toRequestBody(MediaType.parse("image/*"))
        var fileBody = MultipartBody.Builder()
            .addFormDataPart("file", file?.name, fileReqBody!!)
            .build()
        var headerBoundary="multipart/form-data;boundary="+ fileBody?.boundary

        CraftExchangeRepository
            .getTransactionService()
            .uploadPaymentDetails(token,headerBoundary,payObjString,fileBody!!).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    uploadPaymentListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if(response?.isSuccessful){
                        uploadPaymentListener?.onSuccess()
                    }else{
                        uploadPaymentListener?.onFailure()
                    }
                }
            })
    }

    fun uploadDeliveryReceipt(enquiryId : Long, orderDispatchDate : String,eta : String ,filePath : String) {
        var repoCall : Call<ResponseBody> ?= null

        var file = File(filePath)
        var fileReqBody = file!!.toRequestBody(MediaType.parse("image/*"))
        var fileBody = MultipartBody.Builder()
            .addFormDataPart("file", file?.name, fileReqBody!!)
            .build()
        var headerBoundary="multipart/form-data;boundary="+ fileBody?.boundary

        if(eta!=""){
            repoCall = CraftExchangeRepository
                .getTransactionService()
                .uploadDeliveryChallan(token,headerBoundary,enquiryId,orderDispatchDate,eta,fileBody!!)
        }else{
            repoCall = CraftExchangeRepository
                .getTransactionService()
                .uploadDeliveryChallanNoEta(token,headerBoundary,enquiryId,orderDispatchDate,fileBody!!)
        }
            repoCall?.enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    uploadPaymentListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if(response?.isSuccessful){
                        uploadPaymentListener?.onSuccess()
                    }else{
                        uploadPaymentListener?.onFailure()
                    }
                }
            })
    }


    fun validateAdvancePayment(enquiryId:Long,paymentStatus:String){
        CraftExchangeRepository
            .getTransactionService()
            .validateAdvancePayment(token,enquiryId,paymentStatus).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    validatePaymentListener?.onPaymentFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    validatePaymentListener?.onPaymentSuccess()
                }
            })
    }

    fun validateFinalPayment(enquiryId:Long,paymentStatus:String){
        CraftExchangeRepository
            .getTransactionService()
            .validateFinalPayment(token,enquiryId,paymentStatus).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    validatePaymentListener?.onPaymentFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    validatePaymentListener?.onPaymentSuccess()
                }
            })
    }

    fun getAdvancePaymentReceipt(enquiryId:Long){
        CraftExchangeRepository
            .getTransactionService()
            .getAdvancePaymentReceipt(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<PaymentReceiptResponse> {
                override fun onFailure(call: Call<PaymentReceiptResponse>, t: Throwable) {
                    paymentReceiptListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<PaymentReceiptResponse>,
                    response: Response<PaymentReceiptResponse>
                ) {
                    if(response.body()?.valid == true){
                        response?.body()?.data?.label?.let { response.body()?.data?.paymentId?.let { it1 ->
                            paymentReceiptListener?.onSuccess(it, it1) } }
                    }else{
                        paymentReceiptListener?.onFailure()
                    }
                }
            })
    }

    fun getFinalPaymentReceipt(enquiryId:Long){
        CraftExchangeRepository
            .getTransactionService()
            .getFinalPaymentReceipt(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<PaymentReceiptResponse> {
                override fun onFailure(call: Call<PaymentReceiptResponse>, t: Throwable) {
                    paymentReceiptListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<PaymentReceiptResponse>,
                    response: Response<PaymentReceiptResponse>
                ) {
                    if(response.body()?.valid == true){
                        response?.body()?.data?.label?.let { response.body()?.data?.paymentId?.let { it1 ->
                            paymentReceiptListener?.onSuccess(it, it1) } }
                    }else{
                        paymentReceiptListener?.onFailure()
                    }
                }
            })
    }

    fun getFinalPaymentDetails(enquiryId:Long){
        CraftExchangeRepository
            .getTransactionService()
            .getFinalPaymentDetails(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<FinalPayDetailsResponse> {
                override fun onFailure(call: Call<FinalPayDetailsResponse>, t: Throwable) {
                    finalPayDetailsListener?.onFPDFailure()
                }
                override fun onResponse(
                    call: Call<FinalPayDetailsResponse>,
                    response: Response<FinalPayDetailsResponse>
                ) {
                    if(response.body()?.valid == true){
                        finalPayDetailsListener?.onFPDSuccess(response?.body()?.data!!)
                    }else{
                        finalPayDetailsListener?.onFPDFailure()
                    }
                }
            })
    }


    fun getAdvancePaymentStatus(enquiryId:Long){
        Log.e("PaymentReceipt", "0000000: $enquiryId")
        CraftExchangeRepository
            .getTransactionService()
            .getAdvancedPaymentStatus(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<AdvancePaymentStatusResponse> {
                override fun onFailure(call: Call<AdvancePaymentStatusResponse>, t: Throwable) {
//                    paymentReceiptListener?.onFailure()
                    advPayListener?.onAdvPayFetchFailure()
                }
                override fun onResponse(
                    call: Call<AdvancePaymentStatusResponse>,
                    response: Response<AdvancePaymentStatusResponse>
                ) {
                    Log.e("PaymentReceipt", "11111 ${response?.body()?.data}")
                    if(response?.body()?.valid!=null){
                        if(response?.body()?.valid!!){
                            advPayListener?.onAdvPayFetchSuccess(response?.body()?.data!!)
                            Log.e("PaymentReceipt", "11111 true ${response?.body()?.data}")
                        }
                        else{
                            advPayListener?.onAdvPayFetchFailure()
                            Log.e("PaymentReceipt", "222222")
                        }
                    }else{
                        advPayListener?.onAdvPayFetchFailure()
                        Log.e("PaymentReceipt", "3333333333 ${response?.code()}")
                        Log.e("PaymentReceipt", "3333333333 ${response?.isSuccessful}")
                        Log.e("PaymentReceipt", "3333333333 ${response?.errorBody()}")
                    }
//                    paymentReceiptListener?.onSuccess()
                }
            })
    }



    fun getOpenTransactions(searchString : String, paymentType : Long){
        CraftExchangeRepository
            .getTransactionService()
            .getAllOpenTransactions(token,paymentType).enqueue(object : Callback, retrofit2.Callback<TransactionResponse> {
                override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                   transactionListener?.onGetTransactionsFailure()
                }
                override fun onResponse(
                    call: Call<TransactionResponse>,
                    response: Response<TransactionResponse>
                ) {
                    if(response.body()?.valid == true){
                        transactionListener?.onGetTransactionsSuccess()
                        TransactionPredicates.insertOngoingTransactions(response?.body()!!,false)
                    }else{
                        transactionListener?.onGetTransactionsFailure()
                    }
                }
            })
    }

    fun getCompletedTransactions(searchString : String, paymentType : Long){
        CraftExchangeRepository
            .getTransactionService()
            .getAllCompletedTransactions(token,paymentType).enqueue(object : Callback, retrofit2.Callback<TransactionResponse> {
                override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                    transactionListener?.onGetTransactionsFailure()
                }
                override fun onResponse(
                    call: Call<TransactionResponse>,
                    response: Response<TransactionResponse>
                ) {
                    if(response.body()?.valid == true){
                        transactionListener?.onGetTransactionsSuccess()
                        TransactionPredicates.insertCompletedTransactions(response?.body()!!,true)
                    }else{
                        transactionListener?.onGetTransactionsFailure()
                    }
                }
            })
    }

    fun getSingleOngoingTransactions(enquiryId : Long){
        Log.e("Transaction","getSingleTransactions :$enquiryId")
        CraftExchangeRepository
            .getTransactionService()
            .getSingleTransaction(token,enquiryId.toInt()).enqueue(object : Callback, retrofit2.Callback<SingleTransactionResponse> {
                override fun onFailure(call: Call<SingleTransactionResponse>, t: Throwable) {
                    transactionListener?.onGetTransactionsFailure()
                    Log.e("Transaction","getSingleTransactions :$t")
                }
                override fun onResponse(
                    call: Call<SingleTransactionResponse>,
                    response: Response<SingleTransactionResponse>
                ) {
                    if(response.body()?.valid == true){
                        TransactionPredicates.insertSingleOngoingTransaction(response?.body()?.data?.ongoingTransactionResponses)//Transaction(response?.body()!!,false)
                        Log.e("Transaction","getSingleTransactions :${response?.body()?.data?.ongoingTransactionResponses?.size}")
                        transactionListener?.onGetTransactionsSuccess()

                    }else{
                        Log.e("Transaction","getSingleTransactions :${response?.body()?.valid}")
                        transactionListener?.onGetTransactionsFailure()
                    }
                }
            })
    }

    fun getSingleCompletedTransactions(enquiryId : Long){
        Log.e("Transaction","getSingleTransactions :$enquiryId")
        CraftExchangeRepository
            .getTransactionService()
            .getSingleTransaction(token,enquiryId.toInt()).enqueue(object : Callback, retrofit2.Callback<SingleTransactionResponse> {
                override fun onFailure(call: Call<SingleTransactionResponse>, t: Throwable) {
                    transactionListener?.onGetTransactionsFailure()
                    Log.e("Transaction","getSingleTransactions :$t")
                }
                override fun onResponse(
                    call: Call<SingleTransactionResponse>,
                    response: Response<SingleTransactionResponse>
                ) {
                    if(response.body()?.valid == true){
                        TransactionPredicates.insertSingleCompletedTransaction(response?.body()?.data?.completedTransactionResponses)//Transaction(response?.body()!!,false)
                        Log.e("Transaction","getSingleTransactions :${response?.body()?.data?.completedTransactionResponses?.size}")
                        transactionListener?.onGetTransactionsSuccess()

                    }else{
                        Log.e("Transaction","getSingleTransactions :${response?.body()?.valid}")
                        transactionListener?.onGetTransactionsFailure()
                    }
                }
            })
    }


    fun getRevisedAdvancedPaymentReceipt(enquiryId:Long){
        CraftExchangeRepository
            .getTransactionService()
            .getRevisedAdvancedPaymentReceipt(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<PaymentReceiptResponse> {
                override fun onFailure(call: Call<PaymentReceiptResponse>, t: Throwable) {
                    paymentReceiptListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<PaymentReceiptResponse>,
                    response: Response<PaymentReceiptResponse>
                ) {
                    if(response.body()?.valid == true){
                        response?.body()?.data?.label?.let { response.body()?.data?.paymentId?.let { it1 ->
                            paymentReceiptListener?.onSuccess(it, it1) } }
                    }else{
                        paymentReceiptListener?.onFailure()
                    }
                }
            })
    }
}