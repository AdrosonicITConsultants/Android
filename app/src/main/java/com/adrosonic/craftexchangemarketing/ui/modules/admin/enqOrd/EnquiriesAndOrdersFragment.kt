package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.EnquiriesAndOrderAdminFragmentBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryOrderCountResponse
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.adapter.EnquiryOrderAdapter
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryOrderViewModel
import com.google.gson.GsonBuilder

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EnquiriesAndOrdersFragment : Fragment(),
EnquiryOrderViewModel.EnquiryOrderCountsInterface{

    val mEOVM : EnquiryOrderViewModel by viewModels()
    private var mUserConfig = UserConfig()
    var countsData : String ?=""
    var enquiryOrderCountResponse : EnquiryOrderCountResponse?= null

    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: EnquiriesAndOrderAdminFragmentBinding?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding?.pbLoader?.visibility=View.VISIBLE

        mEOVM.countsListener =this
        if(Utility.checkIfInternetConnected(requireContext())){
            mEOVM?.getEnquiryOrderCounts()
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())

        }
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.enquiries_and_order_admin_fragment, container, false)
//        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        return mBinding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onCountsSuccess() {
        countsData = mUserConfig.CountsResponse.toString()
        val gson = GsonBuilder().create()
        enquiryOrderCountResponse = gson.fromJson(countsData, EnquiryOrderCountResponse::class.java)
        Log.d(TAG, "onCountsSuccess: " + enquiryOrderCountResponse)
        mBinding?.EnquiryOrderViewPager?.adapter = EnquiryOrderAdapter(requireContext(),childFragmentManager)
        mBinding?.EnquiryOrdertabLayout?.setupWithViewPager(mBinding?.EnquiryOrderViewPager)
        mBinding?.escalationsNo?.text = enquiryOrderCountResponse?.data!![0]?.escaltions.toString()
        mBinding?.redirectEnquiriesNo?.text = enquiryOrderCountResponse?.data!![0]?.awaitingMoq.toString()
        mBinding?.pbLoader?.visibility=View.GONE


    }
    companion object {
        fun newInstance() = EnquiriesAndOrdersFragment()
        const val TAG = "EnquiriesAndOrders"
    }

}