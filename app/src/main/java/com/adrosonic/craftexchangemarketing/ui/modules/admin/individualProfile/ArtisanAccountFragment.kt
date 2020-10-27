package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ArtisanAccountFragmnetBinding
import com.adrosonic.craftexchangemarketing.databinding.ArtisanProfileFragmentLayoutBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserProfileResponse
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.gson.GsonBuilder

class ArtisanAccountFragment : Fragment(){
    private  var mBinding : ArtisanAccountFragmnetBinding? = null
    private var mUserConfig = UserConfig()
    var indUserData : String ?=""
    var userProfileResponse : UserProfileResponse?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_login, container, false)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.artisan_account_fragmnet, container, false)
        return mBinding?.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        indUserData = mUserConfig.indUserDataJson.toString()
        val gson = GsonBuilder().create()
        userProfileResponse = gson.fromJson(indUserData, UserProfileResponse::class.java)
        Log.d("API data ", "onProfileSuccess:" +  userProfileResponse )
//        mBinding?.artisanCluster?.text = userProfileResponse?.data?.cluster
//        mBinding?.artisanBrandName?.text = userProfileResponse?.data?.companyDetails?.companyName
////        var listOfProducts = List
        var itr = userProfileResponse?.data?.paymentAccountDetails?.iterator()
//        Log.d("uuuu", "here  "+userProfileResponse?.data?.productCategories?.get(0))
        var str = ""
        if (itr != null) {
            while (itr.hasNext())
            {
                var data = itr.next()
                when(data.accountType.id){
                    1.toLong()-> {
                        mBinding?.artisanAccount?.text = data.accNo_UPI_Mobile
                        mBinding?.artisanBenificiaryName?.text = data.name
                        mBinding?.artisanBank?.text = data.bankName
                        mBinding?.artisanBranch?.text = data.branch
                    }
                }
//                Log.d("uuuu", "jjh"+ data)
//                str = "$str$data,"
//                Log.d("uuuu", "jjh"+ str)


            }
        }
//        var str1 = str.substring(0, (str.length - 1).coerceAtLeast(0))
//        mBinding?.artisanProductCategory?.text = str1
//
//        var image = userProfileResponse?.data?.companyDetails?.logo
//        var url = Utility.getBrandLogoUrl(userProfileResponse?.data?.id?.toLong() , image)
//        ImageSetter.setImage(
//            requireActivity(),
//            url!!,
//            mBinding?.artisanBrandLogo!!,
//            R.drawable.buyer_logo_placeholder,
//            R.drawable.buyer_logo_placeholder,
//            R.drawable.buyer_logo_placeholder
//        )
    }
}