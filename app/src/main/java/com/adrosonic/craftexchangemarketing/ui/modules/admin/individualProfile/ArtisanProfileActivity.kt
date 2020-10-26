package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityIndArtisanProfileBinding
import com.adrosonic.craftexchangemarketing.databinding.ActivityIndBuyerProfileBinding
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.adapter.AdminDatabaseAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.dashboard.OpenEnquirySummaryActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_ind_artisan_profile.view.*


fun Context.ArtisanProfileIntent(): Intent {
    return Intent(this, OpenEnquirySummaryActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}
class ArtisanProfileActivity  : AppCompatActivity() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private var mBinding : ActivityIndArtisanProfileBinding?= null
    var id : Int ?= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityIndArtisanProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        mBinding?.menuartisanProfileIcon?.setOnClickListener {
            mBinding?.layoutForMenuArtisan?.visibility = View.VISIBLE
            mBinding?.menuArtisanProfile?.visibility = View.VISIBLE
            mBinding?.giveRating?.visibility = View.GONE
        }
        mBinding?.layoutForMenuArtisan?.setOnClickListener {
            mBinding?.layoutForMenuArtisan?.visibility = View.GONE
        }
        mBinding?.menuArtisanProfile?.setOnClickListener {
        }
        mBinding?.editRating?.setOnClickListener {
            mBinding?.menuArtisanProfile?.visibility = View.GONE
            mBinding?.giveRating?.visibility = View.VISIBLE
        }
        mBinding?.cancelEditRating?.setOnClickListener {
            mBinding?.layoutForMenuArtisan?.visibility = View.GONE
        }
        mBinding?.closeEdit?.setOnClickListener {
            mBinding?.layoutForMenuArtisan?.visibility = View.GONE
        }
//        mBinding?.cancelEditRating?.setOnClickListener {
//
//        }
//        childFragmentManager.let {
//
//        }
//        tabLayout = findViewById(R.id.profileTabLayout)
//        viewPager = findViewById(R.id.artisanDetailsPager)
//        val adapter = ArtisanProfileAdapter(this, supportFragmentManager)
//        val adapter = ArtisanProfileAdapter(this,supportFragmentManager)
        mBinding?.artisanDetailsPager?.adapter = ArtisanProfileAdapter(this,supportFragmentManager)
        mBinding?.profileTabLayout?.setupWithViewPager(mBinding?.artisanDetailsPager)

    }

}