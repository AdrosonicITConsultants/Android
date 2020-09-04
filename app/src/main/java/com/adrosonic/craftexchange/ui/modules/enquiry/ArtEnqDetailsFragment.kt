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
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtEnqDetailsBinding
import com.adrosonic.craftexchange.ui.modules.enquiry.adapter.ArtisanEnqDetailsAdapter
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtEnqDetailsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentArtEnqDetailsBinding?= null
    var enqID : Long ?= 0
    var enqDetails :OngoingEnquiries ?= null
    var list = ArrayList<Long>()
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_art_enq_details, container, false)
        if(param1!=null){
            enqID = param1?.toLong()
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.let{
            mBinding?.viewPagerArtEnqDetails?.adapter = ArtisanEnqDetailsAdapter(it)
            mBinding?.tabLayoutArtEnqDetails?.setupWithViewPager(mBinding?.viewPagerArtEnqDetails)
        }

        enqDetails = enqID?.let { mEnqVM.getSingleEnqMutableData(it) }?.value

        var brandUrl = Utility.getBrandLogoUrl(enqDetails?.userId,enqDetails?.logo)
        mBinding?.brandImage?.let {
            ImageSetter?.setImage(requireActivity(),brandUrl,
                it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }

        var profileUrl = Utility.getProfilePhotoUrl(enqDetails?.userId,enqDetails?.profileImage) //TODO Implement profile photo
        mBinding?.profileImage?.let {
            ImageSetter?.setImage(requireActivity(),profileUrl,
                it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }

        mBinding?.clusterName?.text = enqDetails?.clusterName ?: ""
        mBinding?.brandName?.text = enqDetails?.ProductBrandName ?: " - "
        mBinding?.artisanName?.text = "${enqDetails?.firstName} ${enqDetails?.lastName ?: ""}"
        mBinding?.artisanDetails?.text = enqDetails?.brandDesc ?: " - "

        //Todo Product Categories
        var catList = EnquiryPredicates?.getProdCatEnq(enqDetails?.userId)
        catList?.forEach {
            it.productCategoryid?.let { it1 -> list.add(it1) }
        }


        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    companion object {
        fun newInstance(param1 : String) =
            ArtEnqDetailsFragment()
                .apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}