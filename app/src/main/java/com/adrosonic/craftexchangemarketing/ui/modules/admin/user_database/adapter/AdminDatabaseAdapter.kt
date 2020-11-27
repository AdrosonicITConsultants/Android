package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.ArtisanDatabaseFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.BuyerDatabaseFragment
import com.adrosonic.craftexchangemarketing.utils.UserConfig

class AdminDatabaseAdapter (fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private var mUserConfig = UserConfig()
    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 ->
            {
                ArtisanDatabaseFragment()
            }
            else-> BuyerDatabaseFragment()
        }
    }

    override fun getCount(): Int {
        if(mUserConfig?.adminUserRoles.equals(3L))return  1
        else return 2
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