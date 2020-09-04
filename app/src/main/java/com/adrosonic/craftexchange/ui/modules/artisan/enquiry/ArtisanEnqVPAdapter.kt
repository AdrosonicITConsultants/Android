package com.adrosonic.craftexchange.ui.modules.artisan.enquiry

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ArtisanEnqVPAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 -> {
                ArtisanOnGoingEnquiryFragment()
            }
            else -> {
                ArtisanOnGoingEnquiryFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Ongoing"
            }
            else -> {
                "Completed"
            }
        }
    }
}