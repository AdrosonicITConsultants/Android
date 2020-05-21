package com.adrosonic.craftexchange.ui.modules.buyer.authentication.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterDetailsBinding

class BuyerRegisterDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = BuyerRegisterDetailsFragment()
    }

    private var mBinding: FragmentBuyerRegisterDetailsBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_details, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        mBinding?.buttonNext?.setOnClickListener{
//            if (savedInstanceState == null) {
//                activity?.supportFragmentManager?.beginTransaction()
//                    ?.replace(R.id.register_container,
//                        BuyerRegisterPasswordFragment.newInstance(),"Register Buyer Details")
//                    ?.addToBackStack(null)
//                    ?.commit()
//            }
//        }
    }

}
