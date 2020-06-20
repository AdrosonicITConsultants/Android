package com.adrosonic.craftexchange.ui.modules.cx_video

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityCXVideoBinding
import com.adrosonic.craftexchange.ui.modules.landing.buyerLandingIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs


fun Context.demoVideoIntent(): Intent {
    return Intent(this, CXVideoActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class CXVideoActivity : AppCompatActivity() {

    private var mBinding : ActivityCXVideoBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCXVideoBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        var profile = Prefs.getLong(ConstantsDirectory.REF_ROLE_ID,0)
        var videoView = mBinding?.demoVideo
        // initiate a video view
        videoView?.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.demo_video))
        videoView?.start()
        videoView?.setOnCompletionListener {
            when(profile){
                1.toLong() ->{}

                2.toLong() -> {
                    startActivity(buyerLandingIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                }
            }
        }

        mBinding?.btnSkipVideo?.setOnClickListener {
            when(profile){
                1.toLong() ->{}

                2.toLong() -> {
                    videoView?.stopPlayback()
                    startActivity(buyerLandingIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}
