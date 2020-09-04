package com.adrosonic.craftexchange.ui.modules.enquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.databinding.FragmentArtEnqDetailsBinding
import com.adrosonic.craftexchange.databinding.FragmentBuyEnqDetailsBinding
import com.adrosonic.craftexchange.ui.modules.enquiry.adapter.ArtisanEnqDetailsAdapter
import com.adrosonic.craftexchange.ui.modules.enquiry.adapter.BuyerEnqDetailsAdapter
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyEnqDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentBuyEnqDetailsBinding?= null
    var enqID : Long ?= 0
    var enqDetails : OngoingEnquiries?= null
    var text : String ?= ""

    val mEnqVM : EnquiryViewModel by viewModels()


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_enq_details, container, false)
        if(param1!=null){
            enqID = param1?.toLong()
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.let{
            mBinding?.viewPagerBuyEnqDetails?.adapter = BuyerEnqDetailsAdapter(it)
            mBinding?.tabLayoutBuyEnqDetails?.setupWithViewPager(mBinding?.viewPagerBuyEnqDetails)
        }

        enqDetails = enqID?.let { mEnqVM.getSingleEnqMutableData(it) }?.value

        var brandUrl = Utility.getBrandLogoUrl(enqDetails?.userId,enqDetails?.logo)
        mBinding?.brandImage?.let {
            ImageSetter?.setImage(requireActivity(),brandUrl,
                it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }

        mBinding?.brandName?.text = enqDetails?.ProductBrandName ?: " - "
        mBinding?.brandDescription?.text = enqDetails?.brandDesc ?: " - "

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    companion object {
        fun newInstance(param1: String) =
            BuyEnqDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}