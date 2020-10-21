package com.adrosonic.craftexchangemarketing.ui.modules.dashboard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityBuyerProfileBinding
import com.adrosonic.craftexchangemarketing.databinding.ActivityDashboardBinding
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.profile.BuyerProfileActivity
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.viewModels.DashboardViewModel
import com.adrosonic.craftexchangemarketing.viewModels.ProfileViewModel
import com.pixplicity.easyprefs.library.Prefs

fun Context.OpenEnquirySummaryIntent(): Intent {
    return Intent(this, OpenEnquirySummaryActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}

class OpenEnquirySummaryActivity : AppCompatActivity() {

    private var mBinding : ActivityDashboardBinding?= null

    var url : String ?= ""
    var dashoardNo : String ?= ""
    val mViewModel : DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        val webSettings = mBinding?.dashboardView?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.builtInZoomControls = true
        dashoardNo = Prefs.getString(ConstantsDirectory.DASHBOARD,"")
        when(dashoardNo){

            "ES" -> {
                mBinding?.dashboardView?.loadUrl(mViewModel?.getOpenEnquirySummaryDashboard())

            }"MER" -> {
                mBinding?.dashboardView?.loadUrl(mViewModel?.getMicroEnterpriseRevenueDashboard())

             }
            "MEBS" -> {
                mBinding?.dashboardView?.loadUrl(mViewModel?.getMicroEnterpriseBusinessSummaryDashboard())

            }

        }




        mBinding?.btnBack?.setOnClickListener {
            onBackPressed()
        }
    }
}