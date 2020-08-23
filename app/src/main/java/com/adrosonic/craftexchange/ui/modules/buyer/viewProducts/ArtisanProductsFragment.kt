package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.BrandList
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtisanProductsBinding
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.BrandAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.BrandViewModel
import com.adrosonic.craftexchange.viewModels.ClusterViewModel
import io.realm.RealmResults

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanProductsFragment : Fragment(),
BrandViewModel.BrandListInterface{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentArtisanProductsBinding ?= null
    private var brandAdapter: BrandAdapter?= null
    private var mBrandList : RealmResults<BrandList>?= null
    private var mFilteredList : RealmResults<BrandList>?=null


    private var mSpinner = mutableListOf<String>()
    private var mClusterList = HashMap<String?,Long?>()
    private var filterBy : String ? = ""

    val mViewModel : BrandViewModel by viewModels()

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_products, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.brandListener = this

        setRecycleList()
        setFilterList()

        mBinding?.swipeBrand?.isEnabled = false

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mViewModel.getAllBrands()
        }

        mViewModel.getBrandListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<BrandList>>{
                mBrandList = it
                brandAdapter?.updateBrandList(mBrandList)
            })

        mBinding?.swipeBrand?.isRefreshing = true
    }

    private fun setRecycleList(){
        mBinding?.artProdRecyclerList?.layoutManager = GridLayoutManager(requireContext(),2,
            RecyclerView.VERTICAL,false)
        brandAdapter = BrandAdapter(requireContext(),mViewModel.getBrandListMutableData().value)
        mBinding?.artProdRecyclerList?.adapter = brandAdapter
    }

    private fun setFilterList(){
        var clusterList = ProductPredicates.getAllClusters()
        mSpinner.clear()
        mSpinner.add("View by Cluster")
        if (clusterList != null) {
            for (size in clusterList){
                Log.i("Stat","$size")
                var cluster = size?.cluster
                var clusterId = size?.clusterid
                mSpinner.add(cluster!!)
                mClusterList.put(cluster,clusterId)
            }
        }
       filterSpinner(requireContext(),mSpinner,mBinding?.filterCluster)
    }

    override fun onResume() {
        super.onResume()
        setFilterList()
    }

    fun filterSpinner(context : Context, array : List<String>, spinner : Spinner?) {
        var adapter= ArrayAdapter(context, android.R.layout.simple_spinner_item, array)
        var filterBy : String
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position > 0){
                    filterBy = parent?.getItemAtPosition(position).toString()
                    Log.e("spin","fil : $filterBy")
                    mFilteredList = ProductPredicates.getFilteredBrands(mClusterList[filterBy])
                    mBrandList?.size
                    mFilteredList?.size
                    brandAdapter?.updateBrandList(mFilteredList)
                }else{
                    brandAdapter?.updateBrandList(mBrandList)
                }
            }
        })
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("clusterList", "OnFailure")
                mBinding?.swipeBrand?.isRefreshing = false
                mViewModel.getBrandListMutableData()
                Utility.displayMessage(
                    "Error while fetching list",
                    requireContext()
                )
            }
            )
        } catch (e: Exception) {
            Log.e("Brandlist", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Brandlist", "OnFailure")
                mBinding?.swipeBrand?.isRefreshing = false
                mViewModel.getBrandListMutableData()
            }
            )
        } catch (e: Exception) {
            Log.e("Brandlist", "Exception onFailure " + e.message)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArtisanProductsFragment()
    }
}
