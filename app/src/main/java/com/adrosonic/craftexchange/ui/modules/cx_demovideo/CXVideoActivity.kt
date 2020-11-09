package com.adrosonic.craftexchange.ui.modules.cx_demovideo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.databinding.ActivityCXVideoBinding
import com.adrosonic.craftexchange.ui.modules.artisan.landing.artisanLandingIntent
import com.adrosonic.craftexchange.ui.modules.buyer.landing.buyerLandingIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.CMSViewModel
import com.pixplicity.easyprefs.library.Prefs


fun Context.demoVideoIntent(): Intent {
    return Intent(this, CXVideoActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class CXVideoActivity : LocaleBaseActivity(),
CMSViewModel.CMSDataInterface{

    private var mBinding : ActivityCXVideoBinding ?= null
    var url : String ?= ""
    var profile : String?= ""
    val mViewModel : CMSViewModel by viewModels()
//    var videoView = mBinding?.demoVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCXVideoBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        mViewModel?.cmsListener = this

        val webSettings = mBinding?.videoview?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.builtInZoomControls = true

        if(Utility.checkIfInternetConnected(this)){
            mViewModel?.getDemoVideo()
        }

        profile = Prefs.getString(ConstantsDirectory.PROFILE,"")

        // initiate a video view
//        videoView?.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.demo_video));
//        videoView?.start()
//        videoView?.setOnCompletionListener {
//            when(profile){
//                1.toLong() ->{
//                    startActivity(artisanLandingIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
//                }
//
//                2.toLong() -> {
//                    startActivity(buyerLandingIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
//                }
//            }
//        }

        mBinding?.btnSkipVideo?.setOnClickListener {
            when(profile){
                ConstantsDirectory.ARTISAN ->{
//                    videoView?.stopPlayback()
                    startActivity(artisanLandingIntent())
                }

                ConstantsDirectory.BUYER -> {
//                    videoView?.stopPlayback()
                    startActivity(buyerLandingIntent())
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun onCMSFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CMS", "OnFailure")
                Utility.displayMessage("No Video",this)
            }
            )
        } catch (e: Exception) {
            Log.e("CMS", "Exception onFailure " + e.message)
        }
    }

    override fun onCMSSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CMS", "onSuccess")
                when(profile){
                    ConstantsDirectory.BUYER -> {
                        url = UserConfig.shared.videoBuyer
                    }

                    ConstantsDirectory.ARTISAN -> {
                        url = UserConfig.shared.videoArtisan
                    }
                }
                mBinding?.videoview?.loadUrl(url)
            }
            )
        } catch (e: Exception) {
            Log.e("CMS", "Exception onFailure " + e.message)
        }
    }
}
