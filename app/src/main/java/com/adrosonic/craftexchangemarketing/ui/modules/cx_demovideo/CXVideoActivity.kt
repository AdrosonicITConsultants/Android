package com.adrosonic.craftexchangemarketing.ui.modules.cx_demovideo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchangemarketing.databinding.ActivityCXVideoBinding
import com.adrosonic.craftexchangemarketing.ui.modules.admin.landing.adminLandingIntent
//import com.adrosonic.craftexchangemarketing.ui.modules.artisan.landing.artisanLandingIntent
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.landing.buyerLandingIntent
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs


fun Context.demoVideoIntent(): Intent {
    return Intent(this, CXVideoActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class CXVideoActivity : AppCompatActivity() {

    private var mBinding : ActivityCXVideoBinding ?= null
//    var videoView = mBinding?.demoVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCXVideoBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

//        var profile = Prefs.getLong(ConstantsDirectory.REF_ROLE_ID,0)
        var profile = 1
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
                1 ->{
//                    videoView?.stopPlayback()
                startActivity(adminLandingIntent())
            }

                2 -> {
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
}
