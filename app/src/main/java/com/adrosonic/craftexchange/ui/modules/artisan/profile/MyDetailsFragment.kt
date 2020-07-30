package com.adrosonic.craftexchange.ui.modules.artisan.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentMyDetailsBinding
import com.adrosonic.craftexchange.ui.modules.artisan.profile.ArtisanProfileActivity.Companion.craftUser
import com.adrosonic.craftexchange.ui.modules.artisan.profile.ArtisanProfileActivity.Companion.regAddr
import com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile.MyDetailsEditProfileFragment
import com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile.artisanEditProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyDetailsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentMyDetailsBinding ?= null


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_details, container, false)

        var profileImage = Utility.craftUser?.profilePic
        var urlPro = Utility.getProfilePhotoUrl(Prefs.getString(ConstantsDirectory.USER_ID, "").toLong(),profileImage)
        ImageSetter.setImageCircleProgress(requireContext(),urlPro,mBinding?.artisanProfileLogo,
            R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)

//        var rating = craftUser?.rating?.toInt()
        var rating = 3.8
        var ratingFloat = rating.times(20)?.toFloat()
        //artisan rating out of 5 (rating*20)
        ratingFloat?.let { mBinding?.artisanProfileLogo?.setValue(it) }


        mBinding?.avgRating?.text = "$rating / 5"

        var username = "${craftUser?.firstName ?: ""} ${craftUser?.lastName ?: ""}"
        mBinding?.name?.text = username
        mBinding?.email?.text = craftUser?.email ?: " - "
        mBinding?.mobile?.text = craftUser?.mobile ?: " - "
        mBinding?.address?.text = regAddr?.line1 ?: " - "

        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding?.btnEditDetails?.setOnClickListener {
            startActivity(context?.artisanEditProfileIntent()?.putExtra("Section","Details"))
        }
    }

    companion object {
        fun newInstance() = MyDetailsFragment()
        const val TAG = "MyDetFrag"
    }
}
