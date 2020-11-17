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
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.databinding.FragmentArtisanOnGoingEnquiryBinding
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.adapter.ArtisanOnGoingRecyclerAdapter
import com.adrosonic.craftexchange.ui.modules.order.adapter.ArtisanOngoingOrderListAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import io.realm.RealmResults

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ArtisanOngoingOrderFragment : Fragment(),
    OrdersViewModel.FetchOrderInterface {

    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentArtisanOnGoingEnquiryBinding?= null

    val mOrderVm : OrdersViewModel by viewModels()

    var mOrderListAdapter : ArtisanOngoingOrderListAdapter?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_on_going_enquiry, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOrderVm.fetchEnqListener =this
        setRecyclerList()

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mBinding?.swipeOngoingEnquiries?.isRefreshing = true
            mOrderVm.getAllOngoingOrders()
        }

        mOrderVm.getOnOrderListMutableData().observe(viewLifecycleOwner, Observer<RealmResults<Orders>> {
                mOrderList = it
                mOrderListAdapter?.updateProductList(mOrderList)
            })


        mBinding?.swipeOngoingEnquiries?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mBinding?.swipeOngoingEnquiries?.isRefreshing = true
                mOrderVm.getAllOngoingOrders()
            }
        }
    }

    private fun setRecyclerList(){
        mBinding?.ongoingEnqRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        mOrderListAdapter = ArtisanOngoingOrderListAdapter(requireContext(), mOrderVm.getOnOrderListMutableData().value!!)
        mBinding?.ongoingEnqRecyclerList?.adapter = mOrderListAdapter
    }

    fun setVisiblities() {
        if (mOrderVm.getOnOrderListMutableData().value?.size!! > 0) {
            mBinding?.ongoingEnqRecyclerList?.visibility = View.VISIBLE
            mBinding?.emptyViewOrders?.visibility = View.GONE
        } else {
            mBinding?.ongoingEnqRecyclerList?.visibility = View.GONE
            mBinding?.emptyViewOrders?.visibility = View.VISIBLE
        }
    }


    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("OngoingEnqList", "OnFailure")
                mBinding?.swipeOngoingEnquiries?.isRefreshing = false
                mOrderVm.getOnOrderListMutableData()
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
                mOrderVm.getOnOrderListMutableData()
                setVisiblities()
            })
        } catch (e: Exception) {
            Log.e("OngoingEnqList", "Exception onFailure " + e.message)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mOrderVm.getAllOngoingOrders()
            mBinding?.swipeOngoingEnquiries?.isRefreshing= true
        }
    }

    companion object {
        fun newInstance() = ArtisanOngoingOrderFragment()
    }
}