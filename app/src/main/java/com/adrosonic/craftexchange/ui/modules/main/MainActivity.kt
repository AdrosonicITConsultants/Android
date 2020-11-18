package com.adrosonic.craftexchange.ui.modules.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.databinding.ActivityMainBinding
import com.adrosonic.craftexchange.ui.modules.artisan.landing.artisanLandingIntent
import com.adrosonic.craftexchange.ui.modules.buyer.landing.buyerLandingIntent
import com.adrosonic.craftexchange.ui.modules.role.roleselectIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.mainIntent(): Intent {
    return Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class MainActivity : LocaleBaseActivity() {
    private var mBinding : ActivityMainBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
    }

    override fun onResume() {
        super.onResume()
        if (Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)) {
//            var isNotification=false
            var title=""
            var text=""
            var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)
            if (intent.extras != null) {
                for (key in intent.extras!!.keySet()) {
                    Log.e(  "notificationManager", "MainActivity Key: $key Value:${ intent.extras!!.getString(key)}" )
                    if(key.equals("title"))title=intent.extras!!.getString(key)?:""
                    if(key.equals("text"))text=intent.extras!!.getString(key)?:""
                }
            }
//            else isNotification=false
//            if(title.isNotEmpty())isNotification=true
            Log.e(  "notificationManager", "title: $title" )
            when(profile){
                ConstantsDirectory.ARTISAN -> {
                   if(title.isNotEmpty()) startActivity(artisanLandingIntent(true))
                    else startActivity(artisanLandingIntent())
                }
                ConstantsDirectory.BUYER -> {
                    if(title.isNotEmpty()) startActivity(buyerLandingIntent(true))
                    else startActivity(buyerLandingIntent())
                }
            }
        } else {
            startActivity(roleselectIntent())
        }
    }
}
