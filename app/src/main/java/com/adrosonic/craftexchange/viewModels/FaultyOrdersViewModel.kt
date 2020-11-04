package com.adrosonic.craftexchange.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import javax.security.auth.callback.Callback

class FaultyOrdersViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        const val TAG = "FaultyOrdersViewModel"
    }

    interface PostReviewInterface{
        fun onPostFailure()
        fun onPostSuccess()
    }

    interface ResolveFaultInterface{
        fun onResolveFailure()
        fun onResolveSuccess()
    }

    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

    var postReviewListener : PostReviewInterface?= null
    var resolveFaultListener : ResolveFaultInterface?= null


    fun postBuyerFaultReview(orderId : String, buyerComment : String, multicheck : String) {
        CraftExchangeRepository
            .getFaultyOrderService()
            .postBuyerFaultReview(token,orderId,buyerComment,multicheck).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    postReviewListener?.onPostFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    if(response?.isSuccessful){
                        postReviewListener?.onPostSuccess()
                    }else{
                        postReviewListener?.onPostFailure()
                    }
                }
            })
    }

    fun postArtisanFaultReview(orderId : String, artisanReviewComment : String, multicheck : String) {
        CraftExchangeRepository
            .getFaultyOrderService()
            .postArtisanFaultReview(token,orderId,artisanReviewComment,multicheck).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    postReviewListener?.onPostFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    if(response?.isSuccessful){
                        postReviewListener?.onPostSuccess()
                    }else{
                        postReviewListener?.onPostFailure()
                    }
                }
            })
    }

    fun markFaultResolved(orderId : String) {
        CraftExchangeRepository
            .getFaultyOrderService()
            .faultResolved(token,orderId.toLong()).enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    resolveFaultListener?.onResolveFailure()
                }
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    if(response?.isSuccessful){
                        resolveFaultListener?.onResolveSuccess()
                    }else{
                        resolveFaultListener?.onResolveFailure()
                    }
                }
            })
    }

}
