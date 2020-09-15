package com.adrosonic.craftexchange.ui.modules.dashboard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityBuyerProfileBinding
import com.adrosonic.craftexchange.databinding.ActivityDashboardBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.viewModels.DashboardViewModel
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.pixplicity.easyprefs.library.Prefs

fun Context.dashboardIntent(): Intent {
    return Intent(this, DashboardActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}

class DashboardActivity : AppCompatActivity() {

    private var mBinding : ActivityDashboardBinding?= null

    var url : String ?= ""
    val mViewModel : DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        val webSettings = mBinding?.dashboardView?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.builtInZoomControls = true

        var profile = Prefs.getString(ConstantsDirectory.PROFILE,"")
        when(profile){
            ConstantsDirectory.ARTISAN -> {
                mBinding?.dashboardView?.loadUrl(mViewModel?.getArtisanDashboard())
            }
            ConstantsDirectory.BUYER -> {
                mBinding?.dashboardView?.loadUrl(mViewModel?.getBuyerDashboard())
            }
        }

        mBinding?.btnBack?.setOnClickListener {
            onBackPressed()
        }
    }
}