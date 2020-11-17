package com.adrosonic.craftexchange.ui.modules.enquiry

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.databinding.FragmentBuyEnqDetailsBinding
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
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
    var enqStatus : Long ?= 0
//    var enqDetails : OngoingEnquiries?= null
    var text : String ?= ""
    var userId=0L
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
        if(param2!=null){
            enqStatus = param2?.toLong()
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.let{
            mBinding?.viewPagerBuyEnqDetails?.adapter =
                enqID?.let { it1 -> enqStatus?.let { it2 -> BuyerEnqDetailsAdapter(it, it1, it2) } }
            mBinding?.tabLayoutBuyEnqDetails?.setupWithViewPager(mBinding?.viewPagerBuyEnqDetails)
        }

        when(enqStatus){
            //Completed
            1L -> {
                var enqDetails = enqID?.let { mEnqVM.getSingleCompEnqData(it) }?.value
                var brandUrl = Utility.getBrandLogoUrl(enqDetails?.userId,enqDetails?.logo)
                mBinding?.brandImage?.let {
                    ImageSetter?.setImage(requireActivity(),brandUrl,
                        it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
                }
                mBinding?.brandName?.text = enqDetails?.ProductBrandName ?: " - "
                mBinding?.brandDescription?.text = enqDetails?.brandDesc ?: " - "

                mBinding?.btnChat?.visibility = View.GONE
            }
            //Ongoing
            2L -> {
                var enqDetails = enqID?.let { mEnqVM.getSingleOnEnqData(it) }?.value
                var brandUrl = Utility.getBrandLogoUrl(enqDetails?.userId,enqDetails?.logo)
                mBinding?.brandImage?.let {
                    ImageSetter?.setImage(requireActivity(),brandUrl,
                        it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
                }
                mBinding?.brandName?.text = enqDetails?.ProductBrandName ?: " - "
                mBinding?.brandDescription?.text = enqDetails?.brandDesc ?: " - "

                mBinding?.btnChat?.visibility = View.VISIBLE
                userId=enqDetails?.userId?:0
            }
        }
        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }
        mBinding?.btnChat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it,userId)))}
        }
    }

    companion object {
        fun newInstance(param1: String,param2: String) =
            BuyEnqDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}