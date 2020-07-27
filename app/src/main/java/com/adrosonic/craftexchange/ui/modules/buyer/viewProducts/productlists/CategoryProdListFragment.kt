package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentCategoryProdListBinding
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.ProductType
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.CategoryProductsAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryProdListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryProdListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentCategoryProdListBinding ?= null

    var productType : String ?= ""
    var categoryId : Long ?= 0
    private var mProduct = mutableListOf<ProductCard>()
    private var catProdAdapter: CategoryProductsAdapter?= null
    private var mSpinner = mutableListOf<String>()
    private var mClusterList = mutableListOf<Pair<Long?,String?>>()
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_prod_list, container, false)
        productType = this.requireArguments().getString(ConstantsDirectory.VIEW_PROD_OF)
        categoryId = this.requireArguments().getString(ConstantsDirectory.PRODUCT_CATEGORY_ID)?.toLong()
        mBinding?.productType?.text = productType
        initializeView()
        catProdAdapter = CategoryProductsAdapter(requireContext(),mProduct)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        catProdAdapter?.setProducts(mProduct)
        mBinding?.categoryProdRecyclerList?.adapter = catProdAdapter
        mBinding?.categoryProdRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
//        mBinding?.catProdRecyclerList?.layoutManager = AutoFitGridLayoutManager(requireContext(),500)
        catProdAdapter?.notifyDataSetChanged()
    }

    private fun initializeView(){
        var productList = ProductPredicates.getCategoryProductsFromId(categoryId)
        mProduct.clear()
        if (productList != null) {
            for (size in productList){
                Log.i("Stat","$size")
                var artisanId = size.artisanId
                var productId = size.productId
                var productTitle = size.productTag
                var status =size.productStatusId
                var desc = size.product_spe
                var prod = ProductCard(artisanId,productId,productTitle,desc,status)
                mProduct.add(prod)
            }
        }

        var clusterList = ProductPredicates.getAllClusters()
        mSpinner.clear()
        mSpinner.add("Filter by Region")
        if (clusterList != null) {
            for (size in clusterList){
                Log.i("Stat","$size")
                var cluster = size?.clusterDesc
                var clusterId = size?.clusterid
                mSpinner.add(cluster!!)
                mClusterList.add(Pair(clusterId,cluster))
            }
        }
        Utility.filterSpinner(requireContext(),mSpinner,mBinding?.filterProduct)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CategoryProdListFragment()
    }
}
