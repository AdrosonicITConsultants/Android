package com.adrosonic.craftexchange.ui.modules.buyer.enquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerEnquiryBinding
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.adapter.BuyerEnqVPAdapter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerEnquiryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentBuyerEnquiryBinding?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_enquiry, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        mBinding?.viewPagerViewEnquiries?.setOnTouchListener { v, event -> true } //disables viewpager swipe

        childFragmentManager.let{
            mBinding?.viewPagerViewEnquiries?.adapter = BuyerEnqVPAdapter(it)
            mBinding?.viewBuyerEnquiriesTab?.setupWithViewPager(mBinding?.viewPagerViewEnquiries)
        }
    }

    companion object {
        fun newInstance() = BuyerEnquiryFragment()
    }
}