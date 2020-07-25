package com.adrosonic.craftexchange.ui.modules.artisan.products

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.adrosonic.craftexchange.databinding.FragmentArtisanHomeBinding
import com.adrosonic.craftexchange.databinding.FragmentArtisanUploadedProductsBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanUploadedProductsFragment : Fragment() {

    private var mBinding: FragmentArtisanUploadedProductsBinding?= null
    var productAdapter : UploadedProductsListAdapter ?= null

    private var param1: String? = null
    private var param2: String? = null
    private var catList = arrayOf<String>()
    var artisanId : Long ?= 0
    var prodCatId : Long ?=0
    var prodCat : String ?= ""
    private var mProduct = mutableListOf<ProductCard>()



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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_uploaded_products, container, false)
        artisanId = this.requireArguments().getLong(ConstantsDirectory.ARTISAN_ID)
        prodCatId = this.requireArguments().getLong(ConstantsDirectory.PRODUCT_CATEGORY_ID)
        prodCat = this.requireArguments().getString(ConstantsDirectory.PRODUCT_CATEGORY)

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.productTitle?.text = prodCat
        initialiseList()
        productAdapter = UploadedProductsListAdapter(requireContext(),mProduct)
        setupRecyclerView()
    }

    fun categoryList(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(catList, object:DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which:Int) {

            }
        })
        builder.create().show()
    }
    private fun setupRecyclerView(){
        mBinding?.categoryRecyclerList?.adapter = productAdapter
        mBinding?.categoryRecyclerList?.layoutManager = LinearLayoutManager(activity,
            LinearLayoutManager.VERTICAL, false)
        productAdapter?.setProducts(mProduct)
        productAdapter?.notifyDataSetChanged()

    }

    private fun initialiseList(){
        var productList = ProductPredicates.getArtisanProductsByCategory(artisanId,prodCatId)
        var size = productList?.size
        mProduct.clear()
        if (productList != null) {
            for (size in productList){
                Log.i("Stat","$size")
                var artisanId = size.artisanId
                var productId = size.productId
                var productTitle = size.productTag
                var status =size.productStatusId
                var desc = size.productSpecs
                var prod = ProductCard(artisanId,productId,productTitle,desc,status)
                mProduct.add(prod)
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = ArtisanUploadedProductsFragment()
    }
}