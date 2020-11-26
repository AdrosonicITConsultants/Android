package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchangemarketing.enums.RedirectEnqTypes
import com.adrosonic.craftexchangemarketing.enums.getString
import com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog.AntaranProductFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog.ArtisanProductFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries.EnquiriesFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.ArtisanDatabaseFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.BuyerDatabaseFragment

class RedirectedEnquiriesAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> EnquiriesFragment(0)
            1 -> EnquiriesFragment(1)
            else -> EnquiriesFragment(2)
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                RedirectEnqTypes.CUSTOM.getString()
            }
            1 -> {
                RedirectEnqTypes.FAULTY.getString()
            }
            else -> {
                RedirectEnqTypes.OTHERS.getString()
            }
        }
    }
}