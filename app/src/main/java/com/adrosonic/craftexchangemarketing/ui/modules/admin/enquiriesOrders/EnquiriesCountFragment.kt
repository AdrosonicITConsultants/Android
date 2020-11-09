package com.adrosonic.craftexchangemarketing.ui.modules.admin.enquiriesOrders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.EnquiriesAndOrderAdminFragmentBinding
import com.adrosonic.craftexchangemarketing.databinding.EnquiryFragmentBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryOrderCountResponse
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.google.gson.GsonBuilder

class EnquiriesCountFragment : Fragment(){
    private var mBinding: EnquiryFragmentBinding?= null
    private var mUserConfig = UserConfig()
    var countsData : String ?=""
    var enquiryOrderCountResponse : EnquiryOrderCountResponse?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.enquiry_fragment, container, false)
//        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countsData = mUserConfig.CountsResponse.toString()
        val gson = GsonBuilder().create()
        enquiryOrderCountResponse = gson.fromJson(countsData, EnquiryOrderCountResponse::class.java)
        mBinding?.ongoingEnquiriesCount?.text = enquiryOrderCountResponse?.data!![0]?.ongoingEnquiries.toString()
        mBinding?.ClosedEnquiryCount1?.text = enquiryOrderCountResponse?.data!![0]?.incompleteAndClosedEnquiries.toString()
        mBinding?.enquiriesConvertedCount?.text = enquiryOrderCountResponse?.data!![0]?.enquiriesConverted.toString()
        mBinding?.AwaitingMoqsResponseCount?.text = enquiryOrderCountResponse?.data!![0]?.awaitingMoqResponse.toString()

    }

}