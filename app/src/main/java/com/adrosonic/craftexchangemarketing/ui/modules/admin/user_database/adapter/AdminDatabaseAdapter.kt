package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.ArtisanDatabaseFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.BuyerDatabaseFragment

class AdminDatabaseAdapter (fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 ->
            {
                ArtisanDatabaseFragment(1)
            }
            else -> {
                ArtisanDatabaseFragment(2)
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Artisan"
            }
            else -> {
                "Buyer"
            }
        }
    }
}