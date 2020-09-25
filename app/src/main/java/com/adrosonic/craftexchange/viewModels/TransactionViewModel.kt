package com.adrosonic.craftexchange.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.repository.data.response.enquiry.payment.PaymentReceiptResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.pixplicity.easyprefs.library.Prefs
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

    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

    var uploadPaymentListener : UploadPaymentInterface ?= null
    var validatePaymentListener : ValidatePaymentInterface ?= null
    var paymentReceiptListener : PaymentReceiptInterface ?= null

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


}