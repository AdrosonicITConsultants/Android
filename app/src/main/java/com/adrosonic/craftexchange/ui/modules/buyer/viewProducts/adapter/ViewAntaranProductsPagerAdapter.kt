package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.CategoryProductsFragment
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.RegionProductsFragment
import com.adrosonic.craftexchange.utils.UserConfig

class ViewAntaranProductsPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
UserConfig.shared.isAntranCoDesign=true
        return  when (position) {
            0 -> {
                RegionProductsFragment()
            }
            else -> {
                CategoryProductsFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Regions"
            }
            else -> {
                "Categories"
            }
        }
    }
}