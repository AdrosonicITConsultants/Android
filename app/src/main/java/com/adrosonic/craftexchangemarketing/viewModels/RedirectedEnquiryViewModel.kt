package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.EnquiryProductDetails
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchangemarketing.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.MoqsPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.ProductCataloguePredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.enquiry.BuyerPayment
import com.adrosonic.craftexchangemarketing.repository.data.request.moq.SendMoqRequest
import com.adrosonic.craftexchangemarketing.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.enquiry.IfExistEnquiryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.marketing.ArtisanDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.GetMoqsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.SendMoqResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.SendSelectedMoqResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.pi.SendPiResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.ArtisanData
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.ArtisanLT8Response
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.CustomEnquiries
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.CustomEnquiriesResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.io.File
import javax.security.auth.callback.Callback

class RedirectedEnquiryViewModel(application: Application) : AndroidViewModel(application){
    companion object {
        const val TAG = "RedirectedEnquiryVM"
    }
    interface ProductDetailsInterface{
        fun onSuccess()
        fun onFailure()
    }
    interface FetchEnquiryInterface{
        fun onFetchFailure()
        fun onFetchSuccess(custEnqList:List<CustomEnquiries>?)
    }
    interface FetchArtisanInterface{
        fun onFetchArtisanFailure()
        fun onFetchArtisanSuccess(list:List<ArtisanData>?)
    }
    var fetchEnqListener : FetchEnquiryInterface?= null
    var prodListener: ProductDetailsInterface? = null
    var artisanListener: FetchArtisanInterface? = null
    val productDetails : MutableLiveData<EnquiryProductDetails> by lazy { MutableLiveData<EnquiryProductDetails>() }

    fun getEnqProductDetails(productId : Long): MutableLiveData<EnquiryProductDetails> {
        productDetails.value = loadEnqProductData(productId)
        return productDetails
    }

    private fun loadEnqProductData(productId : Long): EnquiryProductDetails? {
        var product= EnquiryPredicates.getEnqProduct(productId,false)
        return product
    }

    fun getAdminCustomIncomingEnquiries(pageNo:Int, sortBy: String, sortType:String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getRedirectEnquiryService()
            .getAdminCustomIncomingEnquiries(token,pageNo,sortBy,sortType)
            .enqueue(object: Callback, retrofit2.Callback<CustomEnquiriesResponse> {
                override fun onFailure(call: Call<CustomEnquiriesResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFetchFailure()
                    Log.e(TAG,"Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<CustomEnquiriesResponse>,
                    response: retrofit2.Response<CustomEnquiriesResponse>) {
                    if(response.body()?.valid == true){
                        Log.e(TAG,"Success: "+response.body()?.errorMessage)
//                        EnquiryPredicates?.insertOngoingEnquiries(response?.body()!!)
//                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onFetchSuccess(response.body()?.data)
                    }else{
                        fetchEnqListener?.onFetchFailure()
                        Log.e(TAG,"Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getAdminOtherIncomingEnquiries(pageNo:Int, sortBy: String, sortType:String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getRedirectEnquiryService()
            .getAdminOtherIncomingEnquiries(token,pageNo,sortBy,sortType)
            .enqueue(object: Callback, retrofit2.Callback<CustomEnquiriesResponse> {
                override fun onFailure(call: Call<CustomEnquiriesResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFetchFailure()
                    Log.e(TAG,"Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<CustomEnquiriesResponse>,
                    response: retrofit2.Response<CustomEnquiriesResponse>) {
                    if(response.body()?.valid == true){
                        Log.e(TAG,"Success: "+response.body()?.data)
//                        EnquiryPredicates?.insertOngoingEnquiries(response?.body()!!)
//                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onFetchSuccess(response.body()?.data)
                    }else{
                        fetchEnqListener?.onFetchFailure()
                        Log.e(TAG,"Failure: "+response.body()?.errorMessage)
                    }
                }

            })
    }

    fun getAdminFaultyIncomingEnquiries(pageNo:Int, sortBy: String, sortType:String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getRedirectEnquiryService()
            .getAdminFaultyIncomingEnquiries(token,pageNo,sortBy,sortType)
            .enqueue(object: Callback, retrofit2.Callback<CustomEnquiriesResponse> {
                override fun onFailure(call: Call<CustomEnquiriesResponse>, t: Throwable) {
                    t.printStackTrace()
                    fetchEnqListener?.onFetchFailure()
                    Log.e(TAG,"Failure: "+t.message)
                }
                override fun onResponse(
                    call: Call<CustomEnquiriesResponse>,
                    response: Response<CustomEnquiriesResponse>) {
                    if(response.body()?.valid == true){
                        Log.e(TAG,"Success: "+response.body()?.errorMessage)
//                        EnquiryPredicates?.insertOngoingEnquiries(response?.body()!!)
//                        EnquiryPredicates?.insertEnqPaymentDetails(response?.body()!!)
//                        EnquiryPredicates?.insertEnqArtisanProductCategory(response?.body()!!)
                        fetchEnqListener?.onFetchSuccess(response.body()?.data)
                    }else{
                        fetchEnqListener?.onFetchFailure()
                        Log.e(TAG,"Failure: "+response.body()?.errorMessage)
                    }                }

            })
    }

    fun getArtisanProduct(productId: Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductCatService()
            .getArtisanProduct(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<ProductDetailsResponse> {
                override fun onFailure(call: Call<ProductDetailsResponse>, t: Throwable) {
                    t.printStackTrace()
                    prodListener?.onFailure()
                    Log.e("ArtisanProduct","onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<ProductDetailsResponse>,
                    response: retrofit2.Response<ProductDetailsResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("ArtisanProduct","onSuccess: ${response?.body()}")
                        ProductCataloguePredicates.insertProductDetails(response.body())
//                        ProductCataloguePredicates.updateCluster(response.body())

                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                prodListener?.onSuccess()
                            }
                        }, 500)
                    }else{
                        prodListener?.onFailure()
                        Log.e("ArtisanProduct","onFailure: "+response.body()?.errorCode)
                    }
                }

            })
    }

    fun getCustomProduct(productId: Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("ProductDetails","onSuccess: ${productId}")
        craftexchangemarketingRepository
            .getProductCatService()
            .getCustomProduct(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<ProductDetailsResponse> {
                override fun onFailure(call: Call<ProductDetailsResponse>, t: Throwable) {
                    t.printStackTrace()
                    prodListener?.onFailure()
                    Log.e(TAG,"onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<ProductDetailsResponse>,
                    response: retrofit2.Response<ProductDetailsResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("ProductDetails","onSuccess: ${response?.body()}")
                        Log.e("ProductDetails","onSuccess: ${response?.body()}")
                        ProductCataloguePredicates.insertCustomProductDetails(response.body())

                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                prodListener?.onSuccess()
                            }
                        }, 500)
                    }else{
                        prodListener?.onFailure()
                        Log.e("ProductDetails","onFailure: "+response.body()?.errorCode)
                    }
                }

            })
    }

    fun getArtisansLessThan8Rating(clusterId: Int,searchStr:String,enqId:Int){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e(TAG,"clusterId: $clusterId")
        Log.e(TAG,"searchStr: $searchStr")
        Log.e(TAG,"enqId: $enqId")
        craftexchangemarketingRepository
            .getRedirectEnquiryService()
            .getArtisansLessThan8Rating(token,clusterId,searchStr,enqId)
            .enqueue(object: Callback, retrofit2.Callback<ArtisanLT8Response> {
                override fun onFailure(call: Call<ArtisanLT8Response>, t: Throwable) {
                    t.printStackTrace()
                    artisanListener?.onFetchArtisanFailure()
                    Log.e(TAG,"onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<ArtisanLT8Response>,
                    response: retrofit2.Response<ArtisanLT8Response>) {
                    if(response.body()?.valid == true){
                        Log.e(TAG,"onResponse if : ${response.body()?.data?.size}")
                        artisanListener?.onFetchArtisanSuccess(response?.body()?.data)
                    }else{
                        artisanListener?.onFetchArtisanFailure()
                        Log.e(TAG,"onResponse else : ${response.body()?.valid}")
                    }
                }

            })
    }

    fun sendCustomEnquiry(userIds:String,enqId:Int){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("SendCutEnq","userIds: $userIds")
        Log.e("SendCutEnq","enqId: $enqId")
        craftexchangemarketingRepository
            .getRedirectEnquiryService()
            .sendCustomEnquiry(token,userIds,enqId)
            .enqueue(object: Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    prodListener?.onFailure()
                    Log.e(TAG,"onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: retrofit2.Response<NotificationReadResponse>) {
                    if(response.body()?.valid == true){
                        prodListener?.onSuccess()
                        Log.e("SendCutEnq","onsucces iff: "+response.body()?.data)
                    }else{
                        prodListener?.onFailure()
                        Log.e("SendCutEnq","onsucces else: "+response.body()?.errorMessage)
                    }
                }

            })
    }
   }


