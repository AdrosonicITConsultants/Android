package com.adrosonic.craftexchange.ui.modules.buyer.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBrandBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity.Companion.craftUser

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandBinding ?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand, container, false)

        mBinding?.gst?.text = craftUser?.gstNo ?: " - "
        mBinding?.cin?.text = craftUser?.cin ?: " - "
        mBinding?.pan?.text = craftUser?.pancard ?: " - "

        mBinding?.name?.text = craftUser?.poc_firstName ?: " - "
        mBinding?.mobile?.text = craftUser?.poc_contactNo ?: " - "
        mBinding?.email?.text = craftUser?.poc_email ?: " - "

        return mBinding?.root
    }

    companion object {
        fun newInstance() = BrandFragment()
        const val TAG = "BrandFrag"
    }
}
