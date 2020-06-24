package com.adrosonic.craftexchange.ui.modules.buyer.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityBuyerProfileBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.buyerProfileIntent(): Intent {
    return Intent(this, BuyerProfileActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class BuyerProfileActivity : AppCompatActivity() {

    private var mBinding : ActivityBuyerProfileBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBuyerProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mBinding?.textFirstname?.text = Prefs.getString(ConstantsDirectory.FIRST_NAME,"Craft")
        mBinding?.textLastname?.text = Prefs.getString(ConstantsDirectory.LAST_NAME,"User")

        supportFragmentManager.let{
            mBinding?.viewPagerDetails?.adapter = ProfilePagerAdapter(it)
            mBinding?.tabLayoutDetails?.setupWithViewPager(mBinding?.viewPagerDetails)
        }

    }
}
