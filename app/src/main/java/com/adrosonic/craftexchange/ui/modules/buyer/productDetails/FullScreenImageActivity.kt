package com.adrosonic.craftexchange.ui.modules.buyer.productDetails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityCatalogueProductDetailsBinding
import com.adrosonic.craftexchange.databinding.ActivityFullScreenImageBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.synnapps.carouselview.ImageListener

fun Context.fullScreenImageIntent(): Intent {
    return Intent(this, FullScreenImageActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

private var mBinding : ActivityFullScreenImageBinding ?= null
private var mUserConfig = UserConfig()


class FullScreenImageActivity : LocaleBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        var imageList = mUserConfig.imageUrlList.toString()
        if(imageList.isNotEmpty()){
            var imageListener = ImageListener { position, imageView ->
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                ImageSetter.setImage(applicationContext, imageUrlList.get(position),imageView)
            }
            mBinding?.carouselFullscreen?.pageCount = imageUrlList.size
            mBinding?.carouselFullscreen?.setImageListener(imageListener)
        }

        mBinding?.btnBack?.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}