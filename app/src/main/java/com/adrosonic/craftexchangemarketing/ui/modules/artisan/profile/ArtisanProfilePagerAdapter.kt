package com.adrosonic.craftexchangemarketing.ui.modules.artisan.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ArtisanProfilePagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                MyDetailsFragment()
            }
            1 -> {
                BrandDetailsFragment()
            }
            else -> {
                BankDetailsFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "My Details"
            }
            1 -> {
                "Brand Details"
            }
            else -> {
                "Bank Details"
            }
        }
    }
}