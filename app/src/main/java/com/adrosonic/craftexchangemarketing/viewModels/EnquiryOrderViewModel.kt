package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.EnquiryDataRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnqiuiryListResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryOrderCountResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
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
    interface EnquiryListInterface {
        fun DataFetchSuccess(dataResponse:EnqiuiryListResponse)
    }
    var DataListener : EnquiryListInterface?= null
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
    fun getEnquiryList(
        availability: Int, buyerBrand: String?, clusterId: Int, enquiryId: String?,
        fromDate: String, madeWithAntaran: Int, pageNo: Long, productCategory: Int,
        statusId: Int?, toDate: String, weaverIdOrBrand: String?
    ){
        var req = EnquiryDataRequest(availability , buyerBrand , clusterId ,
            enquiryId,fromDate,madeWithAntaran,pageNo,productCategory,statusId,toDate,weaverIdOrBrand)
        Log.d(TAG, "getEnquiryList: "+ req)
        Log.d(TAG, "getEnquiryList: Function called")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEnquiryOrderService()
            .getEnquiryList(token , EnquiryDataRequest(availability , buyerBrand , clusterId ,
                enquiryId,fromDate,madeWithAntaran,pageNo,productCategory,statusId,toDate,weaverIdOrBrand))
            .enqueue(object : Callback, retrofit2.Callback<EnqiuiryListResponse> {
                override fun onFailure(call: Call<EnqiuiryListResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "onResponse getEnquiryList API call fail")
                }

                override fun onResponse(
                    call: Call<EnqiuiryListResponse>,
                    response: Response<EnqiuiryListResponse>
                ) {
                    if (response.body()?.valid != null) {
                        Log.d(TAG, "onResponse getEnquiryList API call valid")

                            var dataResponse : EnqiuiryListResponse?=null
                                dataResponse = response.body()
                        Log.d(TAG, "assigned api data "+dataResponse)
                        DataListener?.DataFetchSuccess(dataResponse!!)
                    } else {
                        Log.d(TAG, "onResponse getEnquiryList API call invalid")

                    }
                }
            })
    }

    fun getEnquiryClosedList(
        availability: Int, buyerBrand: String?, clusterId: Int, enquiryId: String?,
        fromDate: String, madeWithAntaran: Int, pageNo: Long, productCategory: Int,
        statusId: Int?, toDate: String, weaverIdOrBrand: String?
    ){
        var req = EnquiryDataRequest(availability , buyerBrand , clusterId ,
            enquiryId,fromDate,madeWithAntaran,pageNo,productCategory,statusId,toDate,weaverIdOrBrand)
        Log.d(TAG, "getEnquiryList: "+ req)
        Log.d(TAG, "getEnquiryList: Function called")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEnquiryOrderService()
            .getEnquiryCompletedList(token , EnquiryDataRequest(availability , buyerBrand , clusterId ,
                enquiryId,fromDate,madeWithAntaran,pageNo,productCategory,statusId,toDate,weaverIdOrBrand))
            .enqueue(object : Callback, retrofit2.Callback<EnqiuiryListResponse> {
                override fun onFailure(call: Call<EnqiuiryListResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "onResponse getEnquiryList API call fail")
                }

                override fun onResponse(
                    call: Call<EnqiuiryListResponse>,
                    response: Response<EnqiuiryListResponse>
                ) {
                    if (response.body()?.valid != null) {
                        Log.d(TAG, "onResponse getEnquiryList API call valid")

                        var dataResponse : EnqiuiryListResponse?=null
                        dataResponse = response.body()
                        Log.d(TAG, "assigned api data "+dataResponse)
                        DataListener?.DataFetchSuccess(dataResponse!!)
                    } else {
                        Log.d(TAG, "onResponse getEnquiryList API call invalid")

                    }
                }
            })
    }

    fun getOrderList(
        availability: Int, buyerBrand: String?, clusterId: Int, enquiryId: String?,
        fromDate: String, madeWithAntaran: Int, pageNo: Long, productCategory: Int,
        statusId: Int?, toDate: String, weaverIdOrBrand: String?
    ){
        var req = EnquiryDataRequest(availability , buyerBrand , clusterId ,
            enquiryId,fromDate,madeWithAntaran,pageNo,productCategory,statusId,toDate,weaverIdOrBrand)
        Log.d(TAG, "getEnquiryList: "+ req)
        Log.d(TAG, "getEnquiryList: Function called")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEnquiryOrderService()
            .getOrderList(token , EnquiryDataRequest(availability , buyerBrand , clusterId ,
                enquiryId,fromDate,madeWithAntaran,pageNo,productCategory,statusId,toDate,weaverIdOrBrand))
            .enqueue(object : Callback, retrofit2.Callback<EnqiuiryListResponse> {
                override fun onFailure(call: Call<EnqiuiryListResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "onResponse getEnquiryList API call fail")
                }

                override fun onResponse(
                    call: Call<EnqiuiryListResponse>,
                    response: Response<EnqiuiryListResponse>
                ) {
                    if (response.body()?.valid != null) {
                        Log.d(TAG, "onResponse getEnquiryList API call valid")

                        var dataResponse : EnqiuiryListResponse?=null
                        dataResponse = response.body()
                        Log.d(TAG, "assigned api data "+dataResponse)
                        DataListener?.DataFetchSuccess(dataResponse!!)
                    } else {
                        Log.d(TAG, "onResponse getEnquiryList API call invalid")

                    }
                }
            })
    }

    fun getOrderIncompletedList(
        availability: Int, buyerBrand: String?, clusterId: Int, enquiryId: String?,
        fromDate: String, madeWithAntaran: Int, pageNo: Long, productCategory: Int,
        statusId: Int?, toDate: String, weaverIdOrBrand: String?
    ){
        var req = EnquiryDataRequest(availability , buyerBrand , clusterId ,
            enquiryId,fromDate,madeWithAntaran,pageNo,productCategory,statusId,toDate,weaverIdOrBrand)
        Log.d(TAG, "getEnquiryList: "+ req)
        Log.d(TAG, "getEnquiryList: Function called")
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getEnquiryOrderService()
            .getOrderIncompletedList(token , EnquiryDataRequest(availability , buyerBrand , clusterId ,
                enquiryId,fromDate,madeWithAntaran,pageNo,productCategory,statusId,toDate,weaverIdOrBrand))
            .enqueue(object : Callback, retrofit2.Callback<EnqiuiryListResponse> {
                override fun onFailure(call: Call<EnqiuiryListResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.d(TAG, "onResponse getEnquiryList API call fail")
                }

                override fun onResponse(
                    call: Call<EnqiuiryListResponse>,
                    response: Response<EnqiuiryListResponse>
                ) {
                    if (response.body()?.valid != null) {
                        Log.d(TAG, "onResponse getEnquiryList API call valid")

                        var dataResponse : EnqiuiryListResponse?=null
                        dataResponse = response.body()
                        Log.d(TAG, "assigned api data "+dataResponse)
                        DataListener?.DataFetchSuccess(dataResponse!!)
                    } else {
                        Log.d(TAG, "onResponse getEnquiryList API call invalid")

                    }
                }
            })
    }
}