package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.ClusterPredicates
import com.adrosonic.craftexchange.databinding.FragmentRegionProductsBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.CLusterResponse
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.Cluster
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.RegionAdapter
import com.adrosonic.craftexchange.utils.Utility
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegionProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentRegionProductsBinding ?= null
    private var mProduct = mutableListOf<Cluster>()
    private var regionAdapter: RegionAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_region_products, container, false)
        refreshClusters()
        initializeView()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regionAdapter = RegionAdapter(requireContext(), mProduct)
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        regionAdapter?.setProducts(mProduct)
        mBinding?.clusterRecyclerList?.adapter = regionAdapter
        mBinding?.clusterRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        regionAdapter?.notifyDataSetChanged()
    }

    private fun initializeView(){
        var clusterList = ClusterPredicates.getAllClusters()
        mProduct.clear()
        if (clusterList != null) {
            for (clustersize in clusterList){
                Log.i("Stat","$clustersize")
                var id = clustersize?.clusterid
                var name = clustersize?.clusterDesc
                var adjective = clustersize?.adjective
                var cluster = Cluster(id!!,name!!,adjective!!)
                mProduct.add(cluster)
            }
        }
    }

    private fun refreshClusters(){
        if(Utility.checkIfInternetConnected(requireContext())) {
            CraftExchangeRepository
                .getClusterService()
                .getAllClusters().enqueue(object : Callback, retrofit2.Callback<CLusterResponse> {
                    override fun onFailure(call: Call<CLusterResponse>, t: Throwable) {
                        t.printStackTrace()
                    }
                    override fun onResponse(
                        call: Call<CLusterResponse>,
                        response: Response<CLusterResponse>
                    ) {
                        if (response.body()?.valid == true) {
                            ClusterPredicates.insertClusters(response.body())
                        } else {
                            Toast.makeText(
                                activity,
                                "${response.body()?.errorMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegionProductsFragment()
        const val TAG = "RegionProducts"
    }
}
