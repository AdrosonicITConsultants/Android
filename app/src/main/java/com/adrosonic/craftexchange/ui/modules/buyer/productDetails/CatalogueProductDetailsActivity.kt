package com.adrosonic.craftexchange.ui.modules.buyer.productDetails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityBuyerLandingBinding
import com.adrosonic.craftexchange.databinding.ActivityCatalogueProductDetailsBinding
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity

fun Context.catalogueProductDetailsIntent(): Intent {
    return Intent(this, CatalogueProductDetailsActivity::class.java)
//        .apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    }
}
private var mBinding : ActivityCatalogueProductDetailsBinding ?= null

class CatalogueProductDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityCatalogueProductDetailsBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        mBinding?.seeAllProdText?.setOnClickListener{
            focusOnView()
        }
        mBinding?.btnBack?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    private fun focusOnView() {
        Handler().post {
            mBinding?.weaveTypeUsedText?.top?.let {
                mBinding?.scrollProductDetails?.scrollTo(0,
                    it
                )
            }
        }
    }
}