package com.adrosonic.craftexchange.ui.modules.enquiry.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchange.ui.modules.enquiry.EnquiryBankFragment
import com.adrosonic.craftexchange.ui.modules.enquiry.EnquiryBrandFragment
import com.adrosonic.craftexchange.ui.modules.enquiry.EnquiryDigPayFragment
import com.adrosonic.craftexchange.ui.modules.enquiry.EnquiryGeneralFragment


class BuyerEnqDetailsAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 -> {
                EnquiryGeneralFragment()
            }
            else -> {
                EnquiryBrandFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "General"
            }
            else -> {
                "Brand"
            }
        }
    }
}