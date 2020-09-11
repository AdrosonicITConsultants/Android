package com.adrosonic.craftexchange.ui.modules.enquiry

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.adrosonic.craftexchange.repository.data.response.marketing.ArtisanDetailsResponse
import com.adrosonic.craftexchange.ui.modules.enquiry.adapter.ArtisanEnqDetailsAdapter
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel

private const val ARG_PARAM1 = "param1"
private const val ARTISAN_ID = "artisanId"

class ArtEnqDetailsFragment : Fragment(),
EnquiryViewModel.ArtisanDetailsInterface{
    private var param1: String? = null
    private var artisanId: Long = 0

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
            artisanId = it.getLong(ARTISAN_ID)?:0
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_art_enq_details, container, false)
        if(param1!=null){
            enqID = if(param1!!.length>0)param1?.toLong() else 0
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(artisanId>0){
            mBinding?.viewPagerArtEnqDetails?.visibility=View.GONE
            mBinding?.tabLayoutArtEnqDetails?.visibility=View.GONE
            mEnqVM?.artisanListener=this
            mEnqVM?.getArtisanProfile(artisanId)
            Utility.displayMessage("Please wait while we are fetching details for you",requireContext())
        }
        else {
            childFragmentManager.let{
                mBinding?.viewPagerArtEnqDetails?.adapter = ArtisanEnqDetailsAdapter(it)
                mBinding?.tabLayoutArtEnqDetails?.setupWithViewPager(mBinding?.viewPagerArtEnqDetails)
            }
            mBinding?.viewPagerArtEnqDetails?.visibility = View.VISIBLE
            mBinding?.tabLayoutArtEnqDetails?.visibility = View.VISIBLE
            enqDetails = enqID?.let { mEnqVM.getSingleEnqMutableData(it) }?.value


            var brandUrl = Utility.getBrandLogoUrl(enqDetails?.userId, enqDetails?.logo)
            mBinding?.brandImage?.let {
                ImageSetter?.setImage(
                    requireActivity(),
                    brandUrl,
                    it,
                    R.drawable.artisan_logo_placeholder,
                    R.drawable.artisan_logo_placeholder,
                    R.drawable.artisan_logo_placeholder
                )
            }

            var profileUrl = Utility.getProfilePhotoUrl(enqDetails?.userId, enqDetails?.profileImage ) //TODO Implement profile photo
            mBinding?.profileImage?.let {
                ImageSetter?.setImage(
                    requireActivity(),
                    profileUrl,
                    it,
                    R.drawable.artisan_logo_placeholder,
                    R.drawable.artisan_logo_placeholder,
                    R.drawable.artisan_logo_placeholder
                )
            }

            mBinding?.clusterName?.text = enqDetails?.clusterName ?: ""
            mBinding?.brandName?.text = enqDetails?.ProductBrandName ?: " - "
            mBinding?.artisanName?.text = "${enqDetails?.firstName} ${enqDetails?.lastName ?: ""}"
            mBinding?.artisanDetails?.text = enqDetails?.brandDesc ?: " - "
        }
        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    companion object {
        fun newInstance(param1 : String,artisanId : Long) = ArtEnqDetailsFragment()
                .apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putLong(ARTISAN_ID, artisanId)
                }
        }

    }

    override fun onFetch(data: ArtisanDetailsResponse?) {

    try {
        Handler(Looper.getMainLooper()).post(Runnable {
        if(data!=null){
            val artisan=data?.data
            mBinding?.clusterName?.text = artisan.cluster ?: ""
            mBinding?.brandName?.text =artisan.companyDetails.companyName ?: " - "
            mBinding?.artisanName?.text = "${artisan?.firstName} ${artisan?.lastName ?: ""}"
            mBinding?.artisanDetails?.text = artisan?.companyDetails.desc ?: " - "
            mBinding?.productCategories?.text = artisan?.productCategories.joinToString ()
            var brandUrl = Utility.getBrandLogoUrl(artisan?.id,artisan?.companyDetails?.logo)
            mBinding?.brandImage?.let {
                ImageSetter?.setImage(requireActivity(),brandUrl, it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
            }

            var profileUrl = Utility.getProfilePhotoUrl(artisan?.id,artisan?.profilePic) //TODO Implement profile photo
            mBinding?.profileImage?.let {
                ImageSetter?.setImage(requireActivity(),profileUrl,
                    it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
            }
        }else Utility.displayMessage("Unable to fetch details, please try again later",requireContext())
                })
        } catch (e: Exception) {
                Log.e("Enquiry Details", "Exception onFailure " + e.message)
            }
    }
}