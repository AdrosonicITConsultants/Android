package com.adrosonic.craftexchangemarketing.ui.modules.buyer.viewProducts

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

import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchangemarketing.databinding.FragmentRegionProductsBinding
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.viewProducts.adapter.RegionAdapter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.ClusterViewModel
import io.realm.RealmResults

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegionProductsFragment : Fragment(),
ClusterViewModel.ClusterProdInterface{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentRegionProductsBinding ?= null
    private var regionAdapter: RegionAdapter?= null
    private var mCLusterList : RealmResults<ClusterList> ?= null
    val mViewModel: ClusterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_region_products, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.clusterListener = this

        setRecyclerList()

        mBinding?.swipeRegion?.isEnabled = false

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mViewModel.getAllClusters()
        }

        mViewModel.getClusterListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<ClusterList>>{
                mCLusterList = it
                regionAdapter?.updateClusterList(mCLusterList)
            })

        mBinding?.swipeRegion?.isRefreshing = true
    }

    fun setRecyclerList(){
        mBinding?.clusterRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        regionAdapter = RegionAdapter(requireContext(), mViewModel.getClusterListMutableData().value)
        mBinding?.clusterRecyclerList?.adapter = regionAdapter

    }

    companion object {
        @JvmStatic
        fun newInstance() = RegionProductsFragment()
        const val TAG = "RegionProducts"
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("clusterList", "OnFailure")
                mBinding?.swipeRegion?.isRefreshing = false
                mViewModel.getClusterListMutableData()
                Utility.displayMessage(
                    "Error while fetching list",
                    requireContext()
                )
            }
            )
        } catch (e: Exception) {
            Log.e("clusterList", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("clusterList", "onSuccess")
                mBinding?.swipeRegion?.isRefreshing = false
                mViewModel.getClusterListMutableData()
            }
            )
        } catch (e: Exception) {
            Log.e("clusterList", "Exception onFailure " + e.message)
        }
    }
}
