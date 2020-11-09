package com.adrosonic.craftexchange.ui.modules.artisan.enquiry

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.CompletedEnquiryFragment

class ArtisanEnqVPAdapter(fragmentManager: FragmentManager,context: Context): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    lateinit var context:Context
    init {
        this.context=context
    }

    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 -> {
                ArtisanOnGoingEnquiryFragment()
            }
            else -> {
                CompletedEnquiryFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                context.getString(R.string.ongoing)
            }
            else -> {
                context.getString(R.string.completed)
            }
        }
    }
}