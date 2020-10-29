package com.adrosonic.craftexchange.ui.modules.artisan.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.ArtisanProductCategory
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.entities.realmEntities.UserAddress
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentBrandDetailsBinding
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile.artisanEditProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandDetailsFragment : Fragment(),
    ProfileViewModel.FetchUserDetailsInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandDetailsBinding ?= null
    val mViewModel: ProfileViewModel by viewModels()
    var craftUser : MutableLiveData<CraftUser>?= null
    var regAddr : MutableLiveData<UserAddress>?= null
    var image : String ?= ""
    var url : String ?= ""

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
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.listener = this
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            refreshProfile()
        }

        mViewModel.getUserMutableData()
            .observe(viewLifecycleOwner, Observer<CraftUser> {
                craftUser = MutableLiveData(it)
            })

        mViewModel.getRegAddrMutableData()
            .observe(viewLifecycleOwner, Observer<UserAddress> {
                regAddr = MutableLiveData(it)
            })

        mBinding?.branddetailsSwipe?.isRefreshing = true
        mBinding?.branddetailsSwipe?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
                mBinding?.branddetailsSwipe?.isRefreshing = false

            } else {
                refreshProfile()
            }
        }

        mBinding?.cluster?.text = craftUser?.value?.clusterdesc ?: " - "
        mBinding?.name?.text = craftUser?.value?.companyName ?: " - "
        mBinding?.description?.text = craftUser?.value?.companyDesc ?: " - "


        mBinding?.prodCategory?.text = getProductCategories()
    }
    fun getProductCategories():String{
        var categories=""
        val jsonProductData = UserConfig.shared.productUploadJson.toString()
        val gson = GsonBuilder().create()
        val productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)
        val arrProductCategory = productUploadData?.data?.productCategories
        val categoriesIdList=ProductPredicates.getProductCategoryIds(UserConfig.shared.userId?.toLong())
        arrProductCategory?.forEach {
            if(categoriesIdList!!.contains(it.id))categories=categories+"${it.productDesc},"
        }
        if (categories.endsWith(",")) {
            categories = categories.substring(0, categories.length - 1);
        }
        if(categories.isEmpty()) return "NA"
        else return categories
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.btnEditBrandDetails?.setOnClickListener {
            startActivity(context?.artisanEditProfileIntent()?.putExtra("Section","Brand"))
        }
    }
    override fun onSuccess() {
//        Utility?.displayMessage("Welcome!",applicationContext)
        Log.e("ArtProBrand","Success")
        mBinding?.branddetailsSwipe?.isRefreshing = false
        setImage()
    }

    override fun onFailure() {
//        Utility?.displayMessage("Error in fetching user details!",applicationContext)
        Log.e("ArtProBrand","Failure")
        mBinding?.branddetailsSwipe?.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        refreshProfile()
    }
    private fun refreshProfile(){
        mViewModel.getArtisanProfileDetails(requireContext())
        craftUser = mViewModel.getUserMutableData()
        regAddr = mViewModel.getRegAddrMutableData()
        setImage()
    }

    fun setImage(){
        image = craftUser?.value?.brandLogo
        url = Utility.getBrandLogoUrl(Prefs.getString(ConstantsDirectory.USER_ID,"").toLong(),image)
        mBinding?.artisanBrandLogo?.let {
            ImageSetter.setImage(requireActivity(),url!!, it,
                R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }
    }

    companion object {
        fun newInstance() = BrandDetailsFragment()
    }
}
