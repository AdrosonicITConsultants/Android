package com.adrosonic.craftexchange.ui.modules.artisan.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBrandDetailsBinding
import com.adrosonic.craftexchange.ui.modules.artisan.profile.ArtisanProfileActivity.Companion.craftUser
import com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile.artisanEditProfileIntent
import com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile.BrandEditFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandDetailsBinding ?= null


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_details, container, false)

        var brandLogo = craftUser?.brandLogo
        var urlBrand = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${Prefs.getString(
            ConstantsDirectory.USER_ID,"")}/CompanyDetails/Logo/${brandLogo}"
        mBinding?.artisanBrandLogo?.let {
            ImageSetter.setImage(requireActivity(),urlBrand, it,
                R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }


        mBinding?.cluster?.text = craftUser?.clusterdesc ?: " - "
        mBinding?.name?.text = craftUser?.companyName ?: " - "
        mBinding?.description?.text = craftUser?.companyDesc ?: " - "
//        mBinding?.prodCategory?.text = craftUser?.p
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.btnEditBrandDetails?.setOnClickListener {
            startActivity(context?.artisanEditProfileIntent()?.putExtra("Section","Brand"))
        }
    }

    companion object {
        fun newInstance() = BrandDetailsFragment()
    }
}
