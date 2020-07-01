package com.adrosonic.craftexchange.ui.modules.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)
            when(profile){
                ConstantsDirectory.ARTISAN -> {
                    startActivity(artisanLandingIntent())
                }
                ConstantsDirectory.BUYER -> {
                    startActivity(buyerLandingIntent())
                }
            }
        } else {
            startActivity(roleselectIntent())
        }
    }
}
