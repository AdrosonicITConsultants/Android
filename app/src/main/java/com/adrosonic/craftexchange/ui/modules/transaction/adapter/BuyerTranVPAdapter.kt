package com.adrosonic.craftexchange.ui.modules.transaction.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.ArtisanOnGoingEnquiryFragment
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.CompletedEnquiryFragment
import com.adrosonic.craftexchange.ui.modules.transaction.BuyerOnGoTransacFragment
import com.adrosonic.craftexchange.ui.modules.transaction.CompTransacFragment


class BuyerTranVPAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 -> {
                BuyerOnGoTransacFragment()
            }
            else -> {
                CompTransacFragment()
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