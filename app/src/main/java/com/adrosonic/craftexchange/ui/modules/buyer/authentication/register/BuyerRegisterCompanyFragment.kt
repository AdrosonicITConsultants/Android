package com.adrosonic.craftexchange.ui.modules.buyer.authentication.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterCompanyBinding

class BuyerRegisterCompanyFragment : Fragment() {

    companion object{
        fun newInstance() = BuyerRegisterCompanyFragment()
    }

    private var mBinding: FragmentBuyerRegisterCompanyBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_company, container, false)
        return mBinding?.root
    }
}
