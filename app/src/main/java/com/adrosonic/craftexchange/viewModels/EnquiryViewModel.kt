package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import javax.security.auth.callback.Callback

class EnquiryViewModel(application: Application) : AndroidViewModel(application){

    interface GenerateEnquiryInterface{
        fun onSuccessEnquiryGeneration(enquiry : GenerateEnquiryResponse)
        fun onExistingEnquiryGeneration(productName : String, id : String)
        fun onFailedEnquiryGeneration()
    }

    var listener: GenerateEnquiryInterface ?= null

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
                            listener?.onSuccessEnquiryGeneration(response?.body()!!)
                            EnquiryPredicates?.insertBuyerEnquiries(response?.body()!!)
//                        }

                    }else{
                        listener?.onFailedEnquiryGeneration()
                        Log.e("Enquiry Generation","Failure: "+response.body()?.errorMessage)
                    }
                }

            })
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
                        if(response?.body()?.data?.ifExists == true){
                            listener?.onExistingEnquiryGeneration(response?.body()?.data?.productName.toString(),response?.body()?.data?.code.toString())
                            response?.body()?.data?.enquiryId?.let {
                                EnquiryPredicates?.updateIfExistEnquiry(productId, it,
                                    response?.body()?.data?.ifExists!!
                                )
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
}


