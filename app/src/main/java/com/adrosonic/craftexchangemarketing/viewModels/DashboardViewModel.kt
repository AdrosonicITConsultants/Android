package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchangemarketing.repository.data.request.dashboard.*
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLEncoder
import java.net.URLEncoder.encode

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    var token = "${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
    var dashUrl : String?= ""
    var email = Prefs.getString(ConstantsDirectory.USER_EMAIL,"")
    var gson = Gson()
    var mQuery : String?= ""
    var buyParams = BuyerDashParam()
    var artParams = ArtDashParam()
    var openEnqSumParams = OpenEnquirySummaryParms()
    var openMicroEnterpriseRevenueParams = OpenMicroEnterpriseRevenueParms()
    var openMicroEnterpriseSummaryParams = OpenMicroEnterpriseSummaryParms()


//    fun getArtisanDashboard() : String? {
//        dashUrl = ConstantsDirectory.ARTISAN_DASHBOARD_URL
//        artParams.token0 = token
//        artParams.token1 = token
//        artParams.token2 = token
//        artParams.token3 = token
//        artParams.token4 = token
//        artParams.token5 = token
//        artParams.token6 = token
//        Log.e("Dashboard","Artisan Params  : $artParams")
//
//        try{
//            mQuery = encode(artParams.toString())
//        }catch(e:UnsupportedEncodingException){
//            // Catch the encoding exception
//            e.printStackTrace();
//        }
//        dashUrl += mQuery
//        Log.e("Dashboard","Url : $dashUrl")
//        return dashUrl
//    }

    fun getOpenEnquirySummaryDashboard() : String? {
        dashUrl = ConstantsDirectory.ADMIN_OPEN_ENQUIRIES_SUMMARY_URL
        openEnqSumParams.token0 = token
        Log.e("Dashboard","Artisan Params  : $artParams")

        try{
            mQuery = encode(openEnqSumParams.toString())
        }catch(e:UnsupportedEncodingException){
            // Catch the encoding exception
            e.printStackTrace();
        }
        dashUrl += mQuery
        Log.e("Dashboard","Url : $dashUrl")
        return dashUrl
    }
    fun getMicroEnterpriseRevenueDashboard() : String? {
        dashUrl = ConstantsDirectory.ADMIN_MICRO_ENTERPRISE_REVENUE_URL
        openMicroEnterpriseRevenueParams.token0 = token
        Log.e("Dashboard","Artisan Params  : $openMicroEnterpriseRevenueParams")

        try{
            mQuery = encode(openMicroEnterpriseRevenueParams.toString())
        }catch(e:UnsupportedEncodingException){
            // Catch the encoding exception
            e.printStackTrace();
        }
        dashUrl += mQuery
        Log.e("Dashboard","Url : $dashUrl")
        return dashUrl
    }
    fun getMicroEnterpriseBusinessSummaryDashboard() : String? {
        dashUrl = ConstantsDirectory.ADMIN_MICRO_ENTERPRISE_SUMMARY_URL
        openMicroEnterpriseSummaryParams.token0 = token
        openMicroEnterpriseSummaryParams.token4 = token
        openMicroEnterpriseSummaryParams.token7 = token
        openMicroEnterpriseSummaryParams.token8 = token
        openMicroEnterpriseSummaryParams.token9 = token


        Log.e("Dashboard","Artisan Params  : $openMicroEnterpriseSummaryParams")

        try{
            mQuery = encode(openMicroEnterpriseSummaryParams.toString())
        }catch(e:UnsupportedEncodingException){
            // Catch the encoding exception
            e.printStackTrace();
        }
        dashUrl += mQuery
        Log.e("Dashboard","Url : $dashUrl")
        return dashUrl
    }
//    fun getBuyerDashboard() : String? {
//        dashUrl = ConstantsDirectory.BUYER_DASHBOARD_URL
//        buyParams.email = email
//        buyParams.token0 = token
//        buyParams.token1 = token
//        Log.e("Dashboard","Buy Params  : $buyParams")
//        try{
//            mQuery = encode(buyParams.toString())
//        }catch(e:UnsupportedEncodingException){
//            // Catch the encoding exception
//            e.printStackTrace();
//        }
//        dashUrl += mQuery
//        Log.e("Dashboard","Url : $dashUrl")
//        return dashUrl
//    }
}