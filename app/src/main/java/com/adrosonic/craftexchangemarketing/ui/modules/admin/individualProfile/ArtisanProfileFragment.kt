package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ArtisanProfileFragmentLayoutBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserProfileResponse
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.google.gson.GsonBuilder


private const val ARG_PARAM1 = "param1"

class ArtisanProfileFragment : Fragment(){

    private var mUserConfig = UserConfig()
    var indUserData : String ?=""
    var userProfileResponse : UserProfileResponse?= null
    private  var mBinding : ArtisanProfileFragmentLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.artisan_profile_fragment_layout, container, false)
        return mBinding?.root
//        return inflater.inflate(R.layout.artisan_profile_fragment_layout, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        indUserData = mUserConfig.indUserDataJson.toString()
        val gson = GsonBuilder().create()
        userProfileResponse = gson.fromJson(indUserData, UserProfileResponse::class.java)
        Log.d("debug", "assigned api data in Profile"+userProfileResponse)
//        while(userProfileResponse == null)
//        {
//
//        }

        mBinding?.artisanEmail?.text = userProfileResponse?.data?.email
        mBinding?.artisanMobileNumber?.text = userProfileResponse?.data?.mobile
        mBinding?.artisanAddress?.text = userProfileResponse?.data?.registeredAddress?.line1 + " "+ userProfileResponse?.data?.registeredAddress?.district + " " + userProfileResponse?.data?.registeredAddress?.state + " " + userProfileResponse?.data?.registeredAddress?.pincode
        }
}