package com.adrosonic.craftexchangemarketing.ui.modules.admin.enquiriesOrders.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enquiriesOrders.EnquiriesCountFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enquiriesOrders.OrderCountFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanAccountFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanBrandFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanProfileFragment


class EnquiryOrderAdapter  (var context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                EnquiriesCountFragment()
            }
            else -> {
                OrderCountFragment()
            }

        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Enquiry"
            }
            else -> {
                "Order"
            }
        }
    }
}
