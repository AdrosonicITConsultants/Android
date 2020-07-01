package com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.databinding.ActivityBuyerEditProfileBinding

fun Context.buyerEditProfileIntent(): Intent {
    return Intent(this, BuyerEditProfileActivity::class.java).apply {
    }
}

class BuyerEditProfileActivity : AppCompatActivity() {

    private var mBinding : ActivityBuyerEditProfileBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBuyerEditProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        supportFragmentManager.let{
            mBinding?.viewPagerEdit?.adapter = EditProfilePagerAdapter(it)
            mBinding?.tabLayoutEdit?.setupWithViewPager(mBinding?.viewPagerEdit)
        }

        mBinding?.btnSave?.setOnClickListener {

        }
    }
}
