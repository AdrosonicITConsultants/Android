package com.adrosonic.craftexchangemarketing.ui.modules.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchangemarketing.databinding.ActivityMainBinding
import com.adrosonic.craftexchangemarketing.ui.modules.admin.landing.adminLandingIntent
//import com.adrosonic.craftexchangemarketing.ui.modules.artisan.landing.artisanLandingIntent
import com.adrosonic.craftexchangemarketing.ui.modules.authentication.login.LoginActivity
import com.adrosonic.craftexchangemarketing.ui.modules.authentication.login.loginIntent
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.landing.buyerLandingIntent
import com.adrosonic.craftexchangemarketing.ui.modules.role.roleselectIntent
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.mainIntent(): Intent {
    return Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class MainActivity : AppCompatActivity() {
    private var mBinding : ActivityMainBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

//        startActivity(roleselectIntent())
    }

    override fun onResume() {
        super.onResume()
        if (Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)) {
//            var isNotification=false
            var title=""
            var text=""
            if (intent.extras != null) {
                for (key in intent.extras!!.keySet()) {
                    Log.e(  "notificationManager", "Key: $key Value:${ intent.extras!!.getString(key)}" )
                    if(key.equals("title"))title=intent.extras!!.getString(key)?:""
                    if(key.equals("text"))text=intent.extras!!.getString(key)?:""
                }
            }
            Log.e(  "notificationManager", "title: $title" )
            if(title.isNotEmpty()) startActivity(adminLandingIntent(true))
            else startActivity(adminLandingIntent())
        } else {
            startActivity(loginIntent())
            Prefs.putString(ConstantsDirectory.PROFILE,"Admin")
        }
    }
}
