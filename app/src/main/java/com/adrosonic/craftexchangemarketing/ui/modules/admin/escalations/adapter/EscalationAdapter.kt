package com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.EscalationChatFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.EscalationFaultyFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.EscalationPaymentFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.EscalationUpdatesFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanAccountFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanBrandFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanProfileFragment

class EscalationAdapter(var context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        return  when (position) {
            0 -> {
                EscalationUpdatesFragment()
            }
            1 -> {
                EscalationChatFragment()
            }
            2 -> {
                EscalationPaymentFragment()
            }
            else -> {
                EscalationFaultyFragment()
            }
        }
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Updates"
            }
            1 -> {
                "Chat"
            }
            2 -> {
                "Payment"
            }
            else -> {
                "Delivery/ Faulty Order"
            }
        }
    }

}