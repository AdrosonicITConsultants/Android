package com.adrosonic.craftexchange.ui.modules.buyer.authentication.reset

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerResetSuccessBinding
import com.adrosonic.craftexchange.ui.modules.authentication.login.LoginActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

class BuyerResetSuccessFragment : Fragment() {

    companion object {
        fun newInstance() = BuyerResetSuccessFragment()
        const val TAG = "BuyerResetSuccess"
    }

    private var mBinding: FragmentBuyerResetSuccessBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_reset_success, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)

        mBinding?.buttonLoginNow?.setOnClickListener {
            when(profile){

                ConstantsDirectory.ARTISAN -> {
                    startActivity(Intent(activity, LoginActivity::class.java).addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                    Prefs.putString(ConstantsDirectory.PROFILE,"Artisan")
                }

                ConstantsDirectory.BUYER -> {
                    startActivity(Intent(activity, LoginActivity::class.java).addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                    Prefs.putString(ConstantsDirectory.PROFILE,"Buyer")
                }

            }
        }
    }
}
