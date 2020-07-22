package com.adrosonic.craftexchange.ui.modules.viewProducts.productlists

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentRegionProdListBinding
import com.adrosonic.craftexchange.ui.modules.viewProducts.adapter.RegionProductsAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegionProdListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegionProdListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentRegionProdListBinding ?= null
    var clusterId : Long ?= 0
    var clusterName : String ?= ""
    var clusterProductAdapter : RegionProductsAdapter ?= null
    private var mProduct = mutableListOf<ProductCard>()

    private var mSpinner = mutableListOf<String>()
    private var filterBy : String ?= ""

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_region_prod_list, container, false)
        clusterId = this.requireArguments().getString(ConstantsDirectory.CLUSTER_ID)?.toLong()
        clusterName = this.requireArguments().getString(ConstantsDirectory.CLUSTER_PRODUCTS)
        mBinding?.productType?.text = clusterName
        initializeView()
        clusterProductAdapter = RegionProductsAdapter(requireContext(),mProduct)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        clusterProductAdapter?.setProducts(mProduct)
        mBinding?.regionProdRecyclerList?.adapter = clusterProductAdapter
        mBinding?.regionProdRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
//        mBinding?.catProdRecyclerList?.layoutManager = AutoFitGridLayoutManager(requireContext(),500)
        clusterProductAdapter?.notifyDataSetChanged()
    }

    private fun initializeView(){
        var productList = ProductPredicates.getClusterProductsFromId(clusterId)
        mProduct.clear()
        if (productList != null) {
            for (size in productList){
                Log.i("Stat","$size")
                var clusterId = size.clusterId
                var productId = size.productId
                var productTitle = size.productTag
                var status =size.productStatusId
                var desc = size.product_spe
                var prod = ProductCard(clusterId,productId,productTitle,desc,status)
                mProduct.add(prod)
            }
        }
        var list =ProductPredicates.getAllCategoryProducts()
        mSpinner.clear()
        mSpinner.add("Filter by product category")
        if (list != null) {
            for (size in list){
                Log.i("Stat","$size")
                var product = size?.product
                mSpinner.add(product!!)
            }
        }
        Utility.filterSpinner(requireContext(),mSpinner,mBinding?.filterRegion)
    }

    companion object {
        fun newInstance() = RegionProdListFragment()
    }
}
