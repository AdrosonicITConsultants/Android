package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.ArtisanDatabaseFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.BuyerDatabaseFragment

class ArtisanProfileAdapter  (var context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getItem(position: Int): Fragment {

        return  when (position) {
            0 -> {
                ArtisanProfileFragment()
            }
            1 -> {
                ArtisanBrandFragment()
            }
            else -> {
                ArtisanAccountFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Profile"
            }
            1 -> {
                "Brand"
            }
            else -> {
                "Account"
            }
        }
    }
}