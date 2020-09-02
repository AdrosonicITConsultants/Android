package com.adrosonic.craftexchange.ui.modules.buyer.enquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentCommonEnquiryBinding
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.ArtisanEnqVPAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.adapter.BuyerEnqVPAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CommonEnquiryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentCommonEnquiryBinding?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_common_enquiry, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        mBinding?.viewPagerViewEnquiries?.setOnTouchListener { v, event -> true } //disables viewpager swipe
        var profile = Prefs.getString(ConstantsDirectory.PROFILE,"")
        when(profile){
            "Artisan" -> {
                childFragmentManager.let{
                    mBinding?.viewPagerViewEnquiries?.adapter = ArtisanEnqVPAdapter(it)
                    mBinding?.viewEnquiriesTab?.setupWithViewPager(mBinding?.viewPagerViewEnquiries)
                }
            }
            "Buyer" -> {
                childFragmentManager.let{
                    mBinding?.viewPagerViewEnquiries?.adapter = BuyerEnqVPAdapter(it)
                    mBinding?.viewEnquiriesTab?.setupWithViewPager(mBinding?.viewPagerViewEnquiries)
                }
            }
        }
    }

    companion object {
        fun newInstance() = CommonEnquiryFragment()
    }
}