package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.repository.data.request.dashboard.ArtDashParam
import com.adrosonic.craftexchange.repository.data.request.dashboard.BuyerDashParam
import com.adrosonic.craftexchange.utils.ConstantsDirectory
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

    fun getArtisanDashboard() : String? {
        dashUrl = ConstantsDirectory.ARTISAN_DASHBOARD_URL
        artParams.token0 = token
        artParams.token1 = token
        artParams.token2 = token
        artParams.token3 = token
        artParams.token4 = token
        artParams.token5 = token
        artParams.token6 = token
        Log.e("Dashboard","Artisan Params  : $artParams")

        try{
            mQuery = encode(artParams.toString())
        }catch(e:UnsupportedEncodingException){
            // Catch the encoding exception
            e.printStackTrace();
        }
        dashUrl += mQuery
        Log.e("Dashboard","Url : $dashUrl")
        return dashUrl
    }

    fun getBuyerDashboard() : String? {
        dashUrl = ConstantsDirectory.BUYER_DASHBOARD_URL
        buyParams.email = email
        buyParams.token0 = token
        buyParams.token1 = token
        Log.e("Dashboard","Buy Params  : $buyParams")
        try{
            mQuery = encode(buyParams.toString())
        }catch(e:UnsupportedEncodingException){
            // Catch the encoding exception
            e.printStackTrace();
        }
        dashUrl += mQuery
        Log.e("Dashboard","Url : $dashUrl")
        return dashUrl
    }
}