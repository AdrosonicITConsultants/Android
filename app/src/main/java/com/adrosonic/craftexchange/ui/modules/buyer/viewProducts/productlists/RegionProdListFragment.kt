package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentRegionProdListBinding
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.RegionProductsAdapter
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
    var clusterProductAdapter : RegionProductsAdapter?= null
    private var mProduct = mutableListOf<ProductCard>()

    private var mSpinner = mutableListOf<String>()
    private var filterBy : String ?= ""

    var productListSize : Int ?= 0
    var filterListSize : Int ?= 0

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
        clusterDescription(clusterName)
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
        filterSpinner(requireContext(),mSpinner,mBinding?.filterRegion)
//        Utility.filterSpinner(requireContext(),mSpinner,mBinding?.filterRegion)
    }

    fun filterSpinner(context : Context, array : List<String>, spinner : Spinner?) {
        var adapter= ArrayAdapter(context, android.R.layout.simple_spinner_item, array)
        var filterBy : String
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                initialList()
                clusterProductAdapter?.setProducts(mProduct)
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
                    var productList = ProductPredicates.getFilteredClusterProducts(clusterId,filterBy)
                    var size = productList?.size
                    mProduct.clear()
                    if (productList != null) {
                        for (size in productList){
                            Log.i("Stat","$size")
                            var clusterId = size.clusterId
                            var productId = size.productId
                            var productTitle = size.productTag
                            var status =size.productStatusId
                            var desc = size.product_spe
                            var isWishlisted = size.isWishlisted
                            var prod = ProductCard(clusterId,productId,productTitle,desc,status,isWishlisted)
                            mProduct.add(prod)
                        }
                    }
                    if(mProduct.size == 0){
                        mBinding?.emptyView?.visibility = View.VISIBLE
                        mBinding?.regionProdRecyclerList?.visibility = View.GONE
                    }else{
                        mBinding?.emptyView?.visibility = View.GONE
                        mBinding?.regionProdRecyclerList?.visibility = View.VISIBLE
                        clusterProductAdapter?.setProducts(mProduct)
                    }


                }else{
                    initialList()
                }
            }
        })
    }

    fun initialList(){
        var productList = ProductPredicates.getClusterProductsFromId(clusterId)
        var size = productList?.size
        mProduct.clear()
        if (productList != null) {
            for (size in productList){
                Log.i("Stat","$size")
                var clusterId = size.clusterId
                var productId = size.productId
                var productTitle = size.productTag
                var status =size.productStatusId
                var desc = size.product_spe
                var isWishlisted = size.isWishlisted
                var prod = ProductCard(clusterId,productId,productTitle,desc,status,isWishlisted)
                mProduct.add(prod)
            }
            if(mProduct.size == 0){
                mBinding?.emptyView?.visibility = View.VISIBLE
                mBinding?.regionProdRecyclerList?.visibility = View.GONE
            }else{
                mBinding?.emptyView?.visibility = View.GONE
                mBinding?.regionProdRecyclerList?.visibility = View.VISIBLE
                clusterProductAdapter?.setProducts(mProduct)
            }
        }
    }

//TODO : to be deleted later...only for demo purpose
    fun clusterDescription(name : String?){
        when(name){
            "Maniabandhan" -> {mBinding?.productTypeDesc?.text = activity?.getString(R.string.Man_text)}
            "Gopalpur" -> {mBinding?.productTypeDesc?.text = activity?.getString(R.string.Gop_text)}
            "Venkatagiri" -> {mBinding?.productTypeDesc?.text = activity?.getString(R.string.Ven_text)}
            "Kamrup" -> {mBinding?.productTypeDesc?.text = activity?.getString(R.string.Kam_text)}
            "Nalbari" -> {mBinding?.productTypeDesc?.text = activity?.getString(R.string.Nal_text)}
            "Dimapur" -> {mBinding?.productTypeDesc?.text = activity?.getString(R.string.Dil_text)}
        }
    }

    override fun onResume() {
        super.onResume()
        clusterProductAdapter?.notifyDataSetChanged()
    }

    companion object {
        fun newInstance() = RegionProdListFragment()
    }
}
