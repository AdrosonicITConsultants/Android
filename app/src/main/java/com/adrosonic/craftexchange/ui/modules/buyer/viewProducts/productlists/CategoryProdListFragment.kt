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
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentCategoryProdListBinding
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.CategoryProductsAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.RegionProductsAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import kotlinx.android.synthetic.main.dialog_gen_enquiry_update_or_new.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryProdListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryProdListFragment : Fragment(),

    EnquiryViewModel.GenerateEnquiryInterface,
    CategoryProductsAdapter.EnquiryGeneratedListener {
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

    val mEnqVM : EnquiryViewModel by viewModels()
    var dialog : Dialog?= null
    var mUser : UserConfig?= null
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
        mBinding?.productType?.text = productType
        initializeView()
        catProdAdapter = CategoryProductsAdapter(requireContext(),mProduct)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEnqVM.listener = this
        catProdAdapter?.enqListener = this
        dialog = Utility.enquiryGenProgressDialog(requireContext())

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
                initialList()
                catProdAdapter?.setProducts(mProduct)
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
                    var productList = ProductPredicates.getFilteredCategoryProducts(categoryId,filterBy)
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
                        mBinding?.categoryProdRecyclerList?.visibility = View.GONE
                    }else{
                        mBinding?.emptyView?.visibility = View.GONE
                        mBinding?.categoryProdRecyclerList?.visibility = View.VISIBLE
                        catProdAdapter?.setProducts(mProduct)
                    }
                }else{
                    initialList()
                }
            }
        })
    }


    fun initialList(){
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
                var isWishlisted = size.isWishlisted
                var prod = ProductCard(artisanId,productId,productTitle,desc,status,isWishlisted)
                mProduct.add(prod)
            }
            if(mProduct.size == 0){
                mBinding?.emptyView?.visibility = View.VISIBLE
                mBinding?.categoryProdRecyclerList?.visibility = View.GONE
            }else{
                mBinding?.emptyView?.visibility = View.GONE
                mBinding?.categoryProdRecyclerList?.visibility = View.VISIBLE
                catProdAdapter?.setProducts(mProduct)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        catProdAdapter?.notifyDataSetChanged()
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
}
