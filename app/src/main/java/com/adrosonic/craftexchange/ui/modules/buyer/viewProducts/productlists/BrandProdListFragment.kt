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
import com.adrosonic.craftexchange.databinding.FragmentBrandProdListBinding
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.BrandProductsAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandProdListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandProdListBinding ?= null
    var brandProductAdapter : BrandProductsAdapter?= null
    private var mProduct = mutableListOf<ProductCard>()
    private var mSpinner = mutableListOf<String>()
    private var filterBy : String ?= ""
    var artisanId : Long ?= 0
    var artisanName : String ?= ""
    var brandName : String ?= ""
    var url : String ?=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        setupRecyclerView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_prod_list, container, false)

        artisanId = this.requireArguments().getString(ConstantsDirectory.ARTISAN_ID)?.toLong()
        artisanName = this.requireArguments().getString(ConstantsDirectory.ARTISAN)
        brandName = this.requireArguments().getString(ConstantsDirectory.COMP_NAME)
        mBinding?.artisanBrand?.text = brandName ?: artisanName
        var logo = this.requireArguments().getString(ConstantsDirectory.BRAND_IMG_NAME)
        var photo = this.requireArguments().getString(ConstantsDirectory.PROFILE_PHOTO_NAME)
        if(logo != null){
            url = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${artisanId}/CompanyDetails/Logo/${logo}"
        }else{
            url = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${artisanId}/ProfilePics/${photo}"
        }

        mBinding?.brandDesc?.text = "By $artisanName"

        ImageSetter.setImage(requireContext(),url!!,mBinding?.logo!!,R.drawable.artisan_logo_placeholder
                ,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)

        initializeView()
        brandProductAdapter = BrandProductsAdapter(requireContext(),mProduct)
        return mBinding?.root
    }

    private fun setupRecyclerView(){

        mBinding?.brandProdRecyclerList?.adapter = brandProductAdapter
        mBinding?.brandProdRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
//        mBinding?.catProdRecyclerList?.layoutManager = AutoFitGridLayoutManager(requireContext(),500)
        brandProductAdapter?.setProducts(mProduct)
        brandProductAdapter?.notifyDataSetChanged()
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
       filterSpinner(requireContext(),mSpinner,mBinding?.filterBrand)
    }

    fun filterSpinner(context : Context, array : List<String>, spinner : Spinner?) {
        var adapter= ArrayAdapter(context, android.R.layout.simple_spinner_item, array)
        var filterBy : String
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                initialList()
                brandProductAdapter?.setProducts(mProduct)
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
                    var productList = ProductPredicates.getFilteredBrandProducts(artisanId,filterBy)
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
                            var prod = ProductCard(clusterId,productId,productTitle,desc,status)
                            mProduct.add(prod)
                        }
                    }
                    brandProductAdapter?.setProducts(mProduct)
                }else{
                    initialList()
                }
            }
        })
    }


    fun initialList(){
        var productList = ProductPredicates.getBrandProductsFromId(artisanId)
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
                var prod = ProductCard(clusterId,productId,productTitle,desc,status)
                mProduct.add(prod)
            }
            brandProductAdapter?.setProducts(mProduct)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandProdListFragment()
        const val TAG = "BrandProducts"
    }
}
