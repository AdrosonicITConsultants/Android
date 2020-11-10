package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryOrderCountResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class EnquiryOrderViewModel(application: Application) : AndroidViewModel(application) {
    companion object{
        const val TAG = "EnquiryOrderViewModel"
    }
    interface EnquiryOrderCountsInterface {
        fun onCountsSuccess()
    }

    var countsListener : EnquiryOrderCountsInterface?= null

    fun getEnquiryOrderCounts(){
        Log.d(TAG, "getEnquiryOrderCounts: Function called")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEnquiryOrderService()
            .getEnquiryOrderHomeCounts(token)
            .enqueue(object : Callback, retrofit2.Callback<EnquiryOrderCountResponse> {
                override fun onFailure(call: Call<EnquiryOrderCountResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "onResponse API call fail")
                }

                override fun onResponse(
                    call: Call<EnquiryOrderCountResponse>,
                    response: Response<EnquiryOrderCountResponse>
                ) {
                    if (response.body()?.valid != null) {
                        Log.d(TAG, "onResponse API call valid")

                        UserConfig.shared.CountsResponse= Gson().toJson(response.body())
//                        Log.d("debug", "assigned api data "+CountsResponse)
                        countsListener?.onCountsSuccess()

                    } else {
                        Log.d(TAG, "onResponse API call invalid")

                    }
                }
            })
    }
}