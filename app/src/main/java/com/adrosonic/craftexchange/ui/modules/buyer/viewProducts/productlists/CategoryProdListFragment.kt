package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentCategoryProdListBinding
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.CatalogueProductsAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.CategoryViewModel
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.dialog_gen_enquiry_update_or_new.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CategoryProdListFragment : Fragment(),
    EnquiryViewModel.GenerateEnquiryInterface,
    CategoryViewModel.CategoryListInterface,
    CatalogueProductsAdapter.EnquiryGeneratedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentCategoryProdListBinding ?= null

    var productType : String ?= ""
    var categoryId : Long ?= 0

    private var catProdAdapter: CatalogueProductsAdapter?= null

    private var mSpinner = mutableListOf<String>()
    private var mClusterList = mutableListOf<Pair<Long?,String?>>()

    private var mCatProdList : RealmResults<ProductCatalogue>?= null
    private var mFilteredList : RealmResults<ProductCatalogue>?= null


    val mEnqVM : EnquiryViewModel by viewModels()
    val mCatVM : CategoryViewModel by viewModels()

    var dialog : Dialog?= null
    var productID : Long ?= 0L


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

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEnqVM.listener = this
        mCatVM.catListener = this

        catProdAdapter?.enqListener = this

        setRecyclerList()
        setFilterList()
        mBinding?.swipeCategoryProducts?.isEnabled = false

        dialog = Utility.enquiryGenProgressDialog(requireContext())
        mBinding?.productType?.text = productType

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            categoryId?.let { mCatVM.getProductsByCategory(it) }
        }
        categoryId?.let {
            mCatVM.getCatProdListMutableData(it)
                .observe(viewLifecycleOwner, Observer<RealmResults<ProductCatalogue>> {
                    mCatProdList = it
                    catProdAdapter?.updateProductList(mCatProdList)
                })
        }
        mBinding?.swipeCategoryProducts?.isRefreshing = true


    }

    private fun setRecyclerList(){
        mBinding?.categoryProdRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        catProdAdapter = CatalogueProductsAdapter(requireContext(),
            categoryId?.let { mCatVM.getCatProdListMutableData(it).value })
        mBinding?.categoryProdRecyclerList?.adapter = catProdAdapter
    }

    private fun setFilterList(){
        var clusterList = ProductPredicates.getAllClusters()
        mSpinner.clear()
        mSpinner.add("Filter by Region")
        if (clusterList != null) {
            for (size in clusterList){
                Log.i("Stat","$size")
                var cluster = size?.cluster
                var clusterId = size?.clusterid
                mSpinner.add(cluster!!)
                mClusterList.add(Pair(clusterId,cluster))
            }
        }
        filterSpinner(requireContext(),mSpinner,mBinding?.filterProduct)
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
                    mFilteredList = ProductPredicates.getFilteredCategoryProducts(categoryId,filterBy)
                    catProdAdapter?.updateProductList(mFilteredList)

                    if(mFilteredList?.size == 0){
                        mBinding?.emptyView?.visibility = View.VISIBLE
                        mBinding?.categoryProdRecyclerList?.visibility = View.GONE
                    }else{
                        mBinding?.emptyView?.visibility = View.GONE
                        mBinding?.categoryProdRecyclerList?.visibility = View.VISIBLE
                    }
                }else{
                catProdAdapter?.updateProductList(mCatProdList)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setFilterList()
    }

    override fun onSuccessEnquiryGeneration(enquiry: GenerateEnquiryResponse) {
        try {
            Handler(Looper.getMainLooper()).post {
                dialog?.cancel()
                Utility.enquiryGenSuccessDialog(requireContext(), enquiry.data.enquiry.code.toString())
                Log.e("EnquiryGeneration", "Onsucces")
            }
        } catch (e: Exception) {
            dialog?.cancel()
            Log.e("EnquiryGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onExistingEnquiryGeneration(productName: String, id: String) {
        try {
            Handler(Looper.getMainLooper()).post {
                dialog?.cancel()
                var exDialog = Utility.enquiryGenExistingDialog(requireContext(),id,productName)
//                exDialog.show()

                exDialog.btn_generate_new_enquiry?.setOnClickListener {
                    exDialog.cancel()
                    dialog?.show()
                    productID?.let { it1 -> mEnqVM.generateEnquiry(it1,false,"Android" ) }
                }
                Log.e("ExistingEnqGeneration", "Onsuccess")
            }
        } catch (e: Exception) {
            dialog?.dismiss()
            Log.e("ExistingEnqGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailedEnquiryGeneration() {
        try {
            Handler(Looper.getMainLooper()).post {dialog?.cancel()
                Log.e("EnquiryGeneration", "onFailure")
                Utility.displayMessage("Enquiry Generation Failed",requireContext())
            }
        } catch (e: Exception) {dialog?.cancel()
            Log.e("EnquiryGeneration", "Exception onFailure " + e.message)
        }
    }

    override fun onEnquiryGenClick(productId: Long, isCustom: Boolean) {
        mEnqVM.ifEnquiryExists(productId,isCustom)
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            dialog?.show()
            mEnqVM.ifEnquiryExists(productId,false)
            productID = productId
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = CategoryProdListFragment()
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("catList", "OnFailure")
                mBinding?.swipeCategoryProducts?.isRefreshing = false
                categoryId?.let { mCatVM.getCatProdListMutableData(it) }
                Utility.displayMessage(
                    "Error while fetching list",
                    requireContext()
                )
            }
            )
        } catch (e: Exception) {
            Log.e("catList", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("catList", "onSuccess")
                mBinding?.swipeCategoryProducts?.isRefreshing = false
                categoryId?.let { mCatVM.getCatProdListMutableData(it) }
            }
            )
        } catch (e: Exception) {
            Log.e("catList", "Exception onFailure " + e.message)
        }
    }
}
