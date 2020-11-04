package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.repository.CMSRepository
import com.adrosonic.craftexchange.repository.data.response.cms.CMSDataResponse
import com.adrosonic.craftexchange.repository.data.response.cms.CMSDataResponseElement
import com.adrosonic.craftexchange.utils.UserConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class CMSViewModel(application: Application) : AndroidViewModel(application) {

    interface CMSDataInterface{
        fun onCMSFailure()
        fun onCMSSuccess()
    }

    var cmsListener : CMSDataInterface?= null

    var cmsDataList : MutableList<CMSDataResponseElement> ?= ArrayList()

    fun getDemoVideo(){
        CMSRepository
            .getCMSservice()
            .getDemoVideo().enqueue(object : Callback, retrofit2.Callback<CMSDataResponse> {
                override fun onFailure(call: Call<CMSDataResponse>, t: Throwable) {
                    t.printStackTrace()
                    cmsListener?.onCMSFailure()
                }
                override fun onResponse(call: Call<CMSDataResponse>, response: Response<CMSDataResponse>) {
                    if (response?.isSuccessful) {
                        cmsListener?.onCMSSuccess()
                        var itr = response?.body()?.iterator()
                        if(itr != null){
                            while (itr.hasNext()){
                                var data = itr.next()
                                UserConfig.shared.videoBuyer = data?.acf?.buyer_demo_video
                                UserConfig.shared.videoArtisan = data?.acf?.artisan_demo_video
                            }
                        }
                    }
                }
            })
    }

    fun getRegionData(){
        CMSRepository
            .getCMSservice()
            .getRegionData().enqueue(object : Callback, retrofit2.Callback<CMSDataResponse> {
                override fun onFailure(call: Call<CMSDataResponse>, t: Throwable) {
                    t.printStackTrace()
//                    cmsListener?.onFailure()
                }
                override fun onResponse(call: Call<CMSDataResponse>, response: Response<CMSDataResponse>) {
                    if (response?.isSuccessful) {
                        UserConfig.shared.regionCMS = Gson().toJson(response?.body())
                    }
                }
            })
    }

    fun getCategoriesData(){
        CMSRepository
            .getCMSservice()
            .getCategoriesData().enqueue(object : Callback, retrofit2.Callback<CMSDataResponse> {
                override fun onFailure(call: Call<CMSDataResponse>, t: Throwable) {
                    t.printStackTrace()
                    cmsListener?.onCMSFailure()
                }
                override fun onResponse(call: Call<CMSDataResponse>, response: Response<CMSDataResponse>) {
                    if (response?.isSuccessful) {
                        UserConfig.shared.categoryCMS = Gson().toJson(response?.body())
                        cmsListener?.onCMSSuccess()
                    }
                }
            })
    }

    fun getPagesData(pageId : Long){
        CMSRepository
            .getCMSservice()
            .getPagesData().enqueue(object : Callback, retrofit2.Callback<CMSDataResponse> {
                override fun onFailure(call: Call<CMSDataResponse>, t: Throwable) {
                    t.printStackTrace()
                    cmsListener?.onCMSFailure()
                }
                override fun onResponse(call: Call<CMSDataResponse>, response: Response<CMSDataResponse>) {
                    if (response?.isSuccessful) {
                        UserConfig.shared.pageCMS = Gson().toJson(response?.body())
                        cmsListener?.onCMSSuccess()
                    }
                }
            })
    }
}