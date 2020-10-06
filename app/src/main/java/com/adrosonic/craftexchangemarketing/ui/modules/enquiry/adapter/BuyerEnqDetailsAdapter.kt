package com.adrosonic.craftexchangemarketing.ui.modules.enquiry.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.enquiry.EnquiryBankFragment
import com.adrosonic.craftexchangemarketing.ui.modules.enquiry.EnquiryBrandFragment
import com.adrosonic.craftexchangemarketing.ui.modules.enquiry.EnquiryDigPayFragment
import com.adrosonic.craftexchangemarketing.ui.modules.enquiry.EnquiryGeneralFragment


class BuyerEnqDetailsAdapter(fragmentManager: FragmentManager , enqID : Long, enqStatus : Long): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    val enqID:Long = enqID
    val enqStatus:Long = enqStatus
    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 -> {
                EnquiryGeneralFragment.newInstance(enqID,enqStatus)
            }
            else -> {
                EnquiryBrandFragment.newInstance(enqID,enqStatus)
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