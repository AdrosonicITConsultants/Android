package com.adrosonic.craftexchange.ui.modules.buyer.enquiry

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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.databinding.FragmentBuyerOngoingEnquiryBinding
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.adapter.BuyerOnGoingRecyclerAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import io.realm.RealmResults


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerOngoingEnquiryFragment : Fragment(),
EnquiryViewModel.FetchEnquiryInterface{

    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentBuyerOngoingEnquiryBinding?= null

    val mEnqVM : EnquiryViewModel by viewModels()

    var mEnqListAdapter : BuyerOnGoingRecyclerAdapter ?= null

    var mEnquiryList : RealmResults<OngoingEnquiries> ?= null


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_ongoing_enquiry, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEnqVM.fetchEnqListener =this
        setRecyclerList()

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mBinding?.swipeOngoingEnquiries?.isRefreshing = true
            mEnqVM.getAllOngoingEnquiries()
        }

        mEnqVM.getOnEnqListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<OngoingEnquiries>> {
                mEnquiryList = it
                mEnqListAdapter?.updateProductList(mEnquiryList)
            })


        mBinding?.swipeOngoingEnquiries?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mBinding?.swipeOngoingEnquiries?.isRefreshing = true
                mEnqVM.getAllOngoingEnquiries()
            }
        }
    }

    private fun setRecyclerList(){
        mBinding?.ongoingEnqRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        mEnqListAdapter = BuyerOnGoingRecyclerAdapter(requireContext(),
            mEnqVM.getOnEnqListMutableData().value!!
        )
        mBinding?.ongoingEnqRecyclerList?.adapter = mEnqListAdapter
//        mEnqListAdapter?.enqListener = this  //important to set adapter first and then call listener
    }

    fun setVisiblities() {
        if (mEnqVM.getOnEnqListMutableData().value?.size!! > 0) {
            mBinding?.ongoingEnqRecyclerList?.visibility = View.VISIBLE
            mBinding?.emptyView?.visibility = View.GONE
        } else {
            mBinding?.ongoingEnqRecyclerList?.visibility = View.GONE
            mBinding?.emptyView?.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BuyerOngoingEnquiryFragment()
    }

    override fun onResume() {
        super.onResume()
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mEnqVM.getAllOngoingEnquiries()
            mBinding?.swipeOngoingEnquiries?.isRefreshing= true
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OngoingEnqList", "OnFailure")
                mBinding?.swipeOngoingEnquiries?.isRefreshing = false
                mEnqVM.getOnEnqListMutableData()
                Utility.displayMessage(getString(R.string.err_fetch_list), requireContext())
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("OngoingEnqList", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OngoingEnqList", "onSuccess")
                mBinding?.swipeOngoingEnquiries?.isRefreshing = false
                mEnqVM.getOnEnqListMutableData()
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("OngoingEnqList", "Exception onFailure " + e.message)
        }
    }
}