package com.adrosonic.craftexchangemarketing.ui.modules.buyer.profile.editProfile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class BuyerEditPPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                GeneralEditFragment()
            }
            1 -> {
                BrandEditFragment()
            }
            else -> {
                DeliveryEditFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "General"
            }
            1 -> {
                "Brand"
            }
            else -> {
                "Delivery"
            }
        }
    }
}