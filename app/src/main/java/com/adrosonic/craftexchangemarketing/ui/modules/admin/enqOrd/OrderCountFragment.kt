package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.OrderFragmentBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryOrderCountResponse
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.google.gson.GsonBuilder

class OrderCountFragment :Fragment(){

    private var mBinding: OrderFragmentBinding?= null
    private var mUserConfig = UserConfig()
    var countsData : String ?=""
    var enquiryOrderCountResponse : EnquiryOrderCountResponse?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.order_fragment, container, false)
//        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countsData = mUserConfig.CountsResponse.toString()
        val gson = GsonBuilder().create()
        enquiryOrderCountResponse = gson.fromJson(countsData, EnquiryOrderCountResponse::class.java)
        mBinding?.ongoingOrderCount?.text = enquiryOrderCountResponse?.data!![0]?.ongoingOrders.toString()
        mBinding?.ClosedOrderCount?.text = enquiryOrderCountResponse?.data!![0]?.incompleteAndClosedOrders.toString()
        mBinding?.CompletedOrder?.text = enquiryOrderCountResponse?.data!![0]?.orderCompletedSuccessfully.toString()
        mBinding?.FaultyOrder?.text = enquiryOrderCountResponse?.data!![0]?.faultyInResolution.toString()
        mBinding?.OngoingOrd?.setOnClickListener {
            val myIntent = Intent(context, EnquiriesDatabaseActivity::class.java)
            myIntent.putExtra("enquiryCount", enquiryOrderCountResponse?.data!![0]?.ongoingOrders)
            myIntent.putExtra("type", 3.toLong())
            startActivity(myIntent)
        }
        mBinding?.IncompleteOrd?.setOnClickListener {
            val myIntent = Intent(context, EnquiriesDatabaseActivity::class.java)
            myIntent.putExtra("enquiryCount", enquiryOrderCountResponse?.data!![0]?.incompleteAndClosedOrders)
            myIntent.putExtra("type", 4.toLong())
            startActivity(myIntent)
        }
        mBinding?.ClosedOrd?.setOnClickListener {
            val myIntent = Intent(context, EnquiriesDatabaseActivity::class.java)
            myIntent.putExtra("enquiryCount", enquiryOrderCountResponse?.data!![0]?.orderCompletedSuccessfully)
            myIntent.putExtra("type", 5.toLong())
            startActivity(myIntent)
        }
    }
}