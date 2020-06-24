package com.adrosonic.craftexchange.ui.modules.buyer.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentDeliveryBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DeliveryFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentDeliveryBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_delivery, container, false)
        return mBinding?.root
    }

    companion object {
        fun newInstance() = DeliveryFragment()
        const val TAG = "DeliveryFrag"
    }
}
