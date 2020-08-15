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
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.entities.realmEntities.UserAddress
import com.adrosonic.craftexchange.databinding.FragmentMyDetailsBinding
import com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile.artisanEditProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyDetailsFragment : Fragment(),
    ProfileViewModel.FetchUserDetailsInterface{

    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentMyDetailsBinding ?= null
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_details, container, false)
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
            .observe(viewLifecycleOwner, Observer<CraftUser>() {
                craftUser = MutableLiveData(it)
            })

        mViewModel.getRegAddrMutableData()
            .observe(viewLifecycleOwner, Observer<UserAddress>() {
                regAddr = MutableLiveData(it)
            })

        mBinding?.mydetailsSwipe?.isRefreshing = true
        mBinding?.mydetailsSwipe?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                mBinding?.mydetailsSwipe?.isRefreshing = false
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                refreshProfile()
            }
        }

        var rating = craftUser?.value?.rating?.toFloat()
        var ratingFloat = rating?.times(10)?.toFloat()
        ratingFloat?.let { mBinding?.artisanProfileLogo?.setValue(it) }
        mBinding?.avgRating?.text = "$rating / 10"
        var username = "${craftUser?.value?.firstName ?: ""} ${craftUser?.value?.lastName ?: ""}"
        mBinding?.name?.text = username
        mBinding?.email?.text = craftUser?.value?.email ?: " - "
        mBinding?.mobile?.text = craftUser?.value?.mobile ?: " - "
        mBinding?.address?.text = regAddr?.value?.line1 ?: " - "

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.btnEditDetails?.setOnClickListener {
            startActivity(context?.artisanEditProfileIntent()
                ?.putExtra("Section","Details"))
        }
    }

    private fun refreshProfile(){
        if(Utility.checkIfInternetConnected(requireContext())) {
            mViewModel?.getArtisanProfileDetails(requireContext())
            craftUser = mViewModel?.getUserMutableData()
            regAddr = mViewModel?.getRegAddrMutableData()
            setImage()
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
        }
    }

    fun setImage(){
        image = craftUser?.value?.profilePic
        url = Utility.getProfilePhotoUrl(Prefs.getString(ConstantsDirectory.USER_ID, "").toLong(),image)
        ImageSetter.setImageCircleProgress(requireContext(),url!!,mBinding?.artisanProfileLogo,
            R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
    }

    override fun onSuccess() {
        Log.e("ArtProPersonal","Success")
        mBinding?.mydetailsSwipe?.isRefreshing = false
        setImage()
    }

    override fun onFailure() {
        Log.e("ArtProPersonal","Failure")
        mBinding?.mydetailsSwipe?.isRefreshing = false

    }

    override fun onResume() {
        super.onResume()
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            refreshProfile()
        }
    }

    companion object {
        fun newInstance() = MyDetailsFragment()
        const val TAG = "MyDetFrag"
    }

}
