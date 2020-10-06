package com.adrosonic.craftexchangemarketing.ui.modules.artisan.products

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentArtisanUploadedProductsBinding
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.ArtisanProductsViewModel
import io.realm.RealmResults


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanUploadedProductsFragment : Fragment(),
ArtisanProductsViewModel.productsFetchInterface{

    private var mBinding: FragmentArtisanUploadedProductsBinding?= null
    var productAdapter : UploadedProductsASearchAdapter ?= null

    private var param1: String? = null
    private var param2: String? = null
    private var mSpinner = ArrayList<String>()
    var artisanId : Long ?= 0
    var prodCatId : Long ?=0
    var prodCat : String ?= ""
    private var mProduct = mutableListOf<ProductCard>()
    val mViewModel: ArtisanProductsViewModel by viewModels()


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
        mBinding?.productTitle?.text = prodCat
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.listener = this
        initializeView()
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mViewModel.getProductsOfArtisan()
            mViewModel.getProductListMutableData(artisanId,prodCat)
        }
        setupRecyclerView()
        mViewModel.getProductListMutableData(artisanId,prodCat)
            .observe(viewLifecycleOwner, Observer<RealmResults<ArtisanProducts>>{
                productAdapter?.updateProductList(it)
            })
        mBinding?.swipeRefreshLayout?.isRefreshing = true
        mBinding?.swipeRefreshLayout?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mViewModel.getProductsOfArtisan()
                mViewModel.getProductListMutableData(artisanId,prodCat)
                initializeView()
            }
        }
    }


    private fun setupRecyclerView(){
        mBinding?.categoryRecyclerList?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        productAdapter = UploadedProductsASearchAdapter(requireContext(), mViewModel.getProductListMutableData(artisanId,prodCat).value)
        mBinding?.categoryRecyclerList?.adapter = productAdapter
    }


    private fun initializeView(){
        var catList = ProductPredicates.getProductCategoriesOfArtisan(artisanId)
        mSpinner.clear()
        mSpinner.add("Change Category")
        if (catList != null) {
            for (size in catList){
                Log.i("Stat","$size")
                size.productCategoryDesc?.let { mSpinner.add(it) }
            }
            setSpinner(mSpinner)
//            filterDialog(mSpinner)
        }
    }


    private fun setSpinner(array : ArrayList<String>){
        var filterBy : String
        var adapter=ArrayAdapter(requireActivity(),R.layout.dark_spinner_text_item, array)
        adapter.setDropDownViewResource(R.layout.dark_spinner_text_item)
        mBinding?.filterCategory?.adapter = adapter
        mBinding?.filterCategory?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

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
//
//                    var productList = ProductPredicates.getFilteredUploadedProducts(artisanId,filterBy)
//                    productAdapter?.updateProductList(productList)
                    prodCat = filterBy
                    mBinding?.productTitle?.text = prodCat
                    mBinding?.filterCategory?.setSelection(0)
                    setupRecyclerView()
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mViewModel.getProductsOfArtisan()
            mViewModel.getProductListMutableData(artisanId,prodCat)
            mBinding?.swipeRefreshLayout?.isRefreshing = true
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CAtegoryList", "Onsuccess")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                initializeView()
                mViewModel.getProductListMutableData(artisanId,prodCat)
            }
            )
        } catch (e: Exception) {
            Log.e("CAtegoryList", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Wishlist", "OnFailure")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                initializeView()
                mViewModel.getProductListMutableData(artisanId,prodCat)
                Utility.displayMessage(
                    "Error while fetching wishlist. Pleas try again after some time",
                    requireContext()
                )
            }
            )
        } catch (e: Exception) {
            Log.e("CAtegoryList", "Exception onFailure " + e.message)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArtisanUploadedProductsFragment()
    }


}