package com.adrosonic.craftexchange.ui.modules.order

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
import com.adrosonic.craftexchange.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.databinding.FragmentCompletedEnquiryBinding
import com.adrosonic.craftexchange.ui.modules.enquiry.adapter.CompletedEnqRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.order.adapter.CompletedOrderListAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import io.realm.RealmResults


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CompletedOrderFragment : Fragment(),
    OrdersViewModel.FetchOrderInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentCompletedEnquiryBinding?= null

    val mOrderVm : OrdersViewModel by viewModels()

    var mOrderListAdapter : CompletedOrderListAdapter?= null

    var mOrderList : RealmResults<Orders>?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_completed_enquiry, container, false)
        return mBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOrderVm.fetchEnqListener =this

        setRecyclerList()
//        setVisiblities()

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mOrderVm.getAllCompletedOrders()
        }

        mBinding?.swipeCompletedEnquiries?.isRefreshing = true
        mOrderVm.getCompOrderListMutableData() .observe(viewLifecycleOwner, Observer<RealmResults<Orders>> {
                mOrderList = it
                mOrderListAdapter?.updateProductList(mOrderList)
            })

        mBinding?.swipeCompletedEnquiries?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mOrderVm.getAllCompletedOrders()
            }
        }
    }

    private fun setRecyclerList(){
        mBinding?.completedEnqRecyclerList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mOrderListAdapter = CompletedOrderListAdapter(requireContext(), mOrderVm.getCompOrderListMutableData().value!!)
        mBinding?.completedEnqRecyclerList?.adapter = mOrderListAdapter
//        mEnqListAdapter?.enqListener = this  //important to set adapter first and then call listener
    }

    fun setVisiblities() {
        if (mOrderVm.getCompOrderListMutableData().value?.size!! > 0) {
            mBinding?.completedEnqRecyclerList?.visibility = View.VISIBLE
            mBinding?.emptyView?.visibility = View.GONE
        } else {
            mBinding?.completedEnqRecyclerList?.visibility = View.GONE
            mBinding?.emptyView?.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mOrderVm.getAllCompletedOrders()
            mBinding?.swipeCompletedEnquiries?.isRefreshing= true
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("compEnqList", "OnFailure")
                mBinding?.swipeCompletedEnquiries?.isRefreshing = false
                mOrderVm.getOnOrderListMutableData()
                setVisiblities()
                Utility.displayMessage("Error while fetching list", requireContext())
            })
        } catch (e: Exception) {
            Log.e("compEnqList", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("compEnqList", "onSuccess")
                mBinding?.swipeCompletedEnquiries?.isRefreshing = false
                mOrderVm.getCompOrderListMutableData()
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("compEnqList", "Exception onFailure " + e.message)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CompletedOrderFragment()
    }

}