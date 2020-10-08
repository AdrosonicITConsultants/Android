package com.adrosonic.craftexchangemarketing.ui.modules.enquiry

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
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchangemarketing.databinding.FragmentArtEnqDetailsBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.marketing.ArtisanDetailsResponse
import com.adrosonic.craftexchangemarketing.ui.modules.enquiry.adapter.ArtisanEnqDetailsAdapter
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARTISAN_ID = "artisanId"

class ArtEnqDetailsFragment : Fragment(),
EnquiryViewModel.ArtisanDetailsInterface
{
    private var param1: String? = null
    private var param2: String? = null
    private var artisanId: Long = 0

    var mBinding : FragmentArtEnqDetailsBinding?= null
    var enqID : Long ?= 0
    var enqStatus : Long ?= 0
    var enqDetails :OngoingEnquiries ?= null
    var list = ArrayList<Long>()
    var text : String ?= ""
    val mEnqVM : EnquiryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        if(param2!=null){
            enqStatus = if(param2!!.length>0)param2?.toLong() else 0
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (artisanId > 0) {
            mBinding?.viewPagerArtEnqDetails?.visibility = View.GONE
            mBinding?.tabLayoutArtEnqDetails?.visibility = View.GONE
            mEnqVM?.artisanListener = this
            mEnqVM?.getArtisanProfile(artisanId)
            Utility.displayMessage(
                "Please wait while we are fetching details for you",
                requireContext()
            )
        } else {
            childFragmentManager.let {
                mBinding?.viewPagerArtEnqDetails?.adapter =
                    enqStatus?.let { it1 ->
                        enqID?.let { it2 ->
                            ArtisanEnqDetailsAdapter(
                                it,
                                it2,
                                it1
                            )
                        }
                    }
                mBinding?.tabLayoutArtEnqDetails?.setupWithViewPager(mBinding?.viewPagerArtEnqDetails)
            }

            when (enqStatus) {
                //Completed
                1L -> {
                    var enqDetails = enqID?.let { mEnqVM.getSingleCompEnqData(it) }?.value
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

                    var profileUrl = Utility.getProfilePhotoUrl(
                        enqDetails?.userId,
                        enqDetails?.profileImage
                    ) //TODO Implement profile photo
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
                    mBinding?.artisanName?.text =
                        "${enqDetails?.firstName} ${enqDetails?.lastName ?: ""}"
                    mBinding?.artisanDetails?.text = enqDetails?.brandDesc ?: " - "

                    mBinding?.btnChat?.visibility = View.GONE
                }
                //Ongoing
                2L -> {
                    var enqDetails = enqID?.let { mEnqVM.getSingleOnEnqData(it) }?.value
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

                    var profileUrl = Utility.getProfilePhotoUrl(
                        enqDetails?.userId,
                        enqDetails?.profileImage
                    ) //TODO Implement profile photo
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
                    mBinding?.artisanName?.text =
                        "${enqDetails?.firstName} ${enqDetails?.lastName ?: ""}"
                    mBinding?.artisanDetails?.text = enqDetails?.brandDesc ?: " - "

                    mBinding?.btnChat?.visibility = View.VISIBLE
                }
            }


            mBinding?.btnBack?.setOnClickListener {
                activity?.onBackPressed()
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

        companion object {
                fun newInstance(param1: String, param2: String, artisanId: Long) =
                    ArtEnqDetailsFragment().apply {
                        arguments = Bundle().apply {
                            putString(ARG_PARAM1, param1)
                            putString(ARG_PARAM2, param2)
                            putLong(ARTISAN_ID, artisanId)
                        }
                    }
            }
    }



