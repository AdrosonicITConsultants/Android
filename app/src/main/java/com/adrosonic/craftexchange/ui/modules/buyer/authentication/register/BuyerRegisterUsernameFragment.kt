package com.adrosonic.craftexchange.ui.modules.buyer.authentication.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterUsernameBinding


class BuyerRegisterUsernameFragment : Fragment() {

    companion object {
        fun newInstance() = BuyerRegisterUsernameFragment()
    }

    private var mBinding: FragmentBuyerRegisterUsernameBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_username, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.buttonVerify?.setOnClickListener{
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.register_container,
                        BuyerRegisterPasswordFragment.newInstance(),"Register Buyer Password")
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
    }
}
