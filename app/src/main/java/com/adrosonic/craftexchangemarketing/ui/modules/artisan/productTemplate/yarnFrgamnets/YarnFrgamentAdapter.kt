package com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.yarnFrgamnets

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class YarnFrgamentAdapter (fragmentManager: FragmentManager,productId:Long,isTemplate:Boolean): FragmentPagerAdapter(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    val productId:Long
    val isTemplate:Boolean
    init {
        this.productId=productId
        this.isTemplate=isTemplate
    }
    override fun getItem(position: Int): Fragment {

    Log.e("YarnFrgamentAdapter","$position")
        return when (position) {
            0 -> {
                WarpFragment.newInstance(productId,isTemplate)
            }
            1 -> {
                WeftFragment.newInstance(productId,isTemplate)
            }
            else -> {
                ExtraWeftFragment.newInstance(productId,isTemplate)
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

}