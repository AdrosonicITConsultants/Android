package com.adrosonic.craftexchange.ui.modules.viewProducts.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchange.ui.modules.viewProducts.CategoryProductsFragment
import com.adrosonic.craftexchange.ui.modules.viewProducts.RegionProductsFragment

class ViewAntaranProductsPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {

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