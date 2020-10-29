package com.adrosonic.craftexchange.ui.modules.enquiry.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchange.ui.modules.enquiry.EnquiryBankFragment
import com.adrosonic.craftexchange.ui.modules.enquiry.EnquiryDigPayFragment
import com.google.android.material.tabs.TabLayout


class ArtisanEnqDetailsAdapter(fragmentManager: FragmentManager,enqID : Long,enqStatus : Long): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    val enqID:Long = enqID
    val enqStatus:Long = enqStatus
    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 -> {
                EnquiryBankFragment.newInstance(enqID,enqStatus)
            }
            else -> {
                EnquiryDigPayFragment.newInstance(enqID,enqStatus)
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Bank Details"
            }
            else -> {
                "Digital Payment Details"
            }
        }
    }
}