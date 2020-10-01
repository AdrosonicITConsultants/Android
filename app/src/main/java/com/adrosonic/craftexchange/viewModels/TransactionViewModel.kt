package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Transactions
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.database.predicates.TransactionPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.repository.data.response.enquiry.payment.PaymentReceiptResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.TransactionResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
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

    interface TransactionInterface{
        fun onGetTransactionsSuccess()
        fun onGetTransactionsFailure()

    }

    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

    var uploadPaymentListener : UploadPaymentInterface ?= null
    var validatePaymentListener : ValidatePaymentInterface ?= null
    var paymentReceiptListener : PaymentReceiptInterface ?= null
    var transactionListener : TransactionInterface ?= null


    val ongoingTranList : MutableLiveData<RealmResults<Transactions>> by lazy { MutableLiveData<RealmResults<Transactions>>() }


    fun getOnTranListMutableData(): MutableLiveData<RealmResults<Transactions>> {
        ongoingTranList.value=loadOnTranList()
        return ongoingTranList
    }

    fun loadOnTranList(): RealmResults<Transactions>?{
        var ongoingTranList = TransactionPredicates.getAllOngoingTransactions()
        return ongoingTranList
    }

    fun uploadPaymentReceipt(payObj : BuyerPayment, filePath : String) {

        var gson = Gson()
        var payObjString = gson.toJson(payObj)

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
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    uploadPaymentListener?.onSuccess()

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

    fun getAdvancePaymentStatus(enquiryId:Long){
//        CraftExchangeRepository
//            .getTransactionService()
//            .getAdvancePaymentReceipt(token,enquiryId).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
////                    paymentReceiptListener?.onFailure()
//                }
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
////                    paymentReceiptListener?.onSuccess()
//                }
//            })
    }

    fun getOpenTransactions(searchString : String, paymentType : Long){
        CraftExchangeRepository
            .getTransactionService()
            .getAllOpenTransactions(token,searchString,paymentType).enqueue(object : Callback, retrofit2.Callback<TransactionResponse> {
                override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                   transactionListener?.onGetTransactionsFailure()
                }
                override fun onResponse(
                    call: Call<TransactionResponse>,
                    response: Response<TransactionResponse>
                ) {
                    if(response.body()?.valid == true){
                        transactionListener?.onGetTransactionsSuccess()
                        TransactionPredicates.insertTransactions(response?.body()!!,false)
                    }else{
                        transactionListener?.onGetTransactionsFailure()
                    }
                }
            })
    }


}