package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists

import android.app.Dialog
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
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentRegionProdListBinding
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.CatalogueProductAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ClusterViewModel
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import io.realm.RealmResults

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegionProdListFragment : Fragment(),
    ClusterViewModel.ClusterProdInterface,
    EnquiryViewModel.GenerateEnquiryInterface,
    CatalogueProductAdapter.EnquiryGeneratedListener{

    private var param1: String? = null
    private var param2: Long? = null

    private var mBinding: FragmentRegionProdListBinding ?= null
    var clusterId : Long ?= 0
    var clusterName : String ?= ""

    var clusterProductAdapter : CatalogueProductAdapter?= null

    private var mClusProductList : RealmResults<ProductCatalogue>?= null
    private var mFilteredList : RealmResults<ProductCatalogue>?= null

    private var mSpinner = mutableListOf<String>()

    val mClusVM : ClusterViewModel by viewModels()
    val mEnqVM : EnquiryViewModel by viewModels()

    var dialog : Dialog ?= null
    var exDialog : Dialog ?= null
    var sucDialog : Dialog ?= null

    var productID : Long ?= 0L
    var enqID : String?=""
    var enqCode : String?=""
    var prodName : String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getLong(ARG_PARAM2)
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
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEnqVM.listener = this
        mClusVM.clusterListener = this

        setRecyclerList()

        setFilterList()
        mBinding?.swipeRegionProducts?.isEnabled = false

        dialog = Utility.enquiryGenProgressDialog(requireContext())
        clusterDescription(clusterName)
        mBinding?.productType?.text = clusterName

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            clusterId?.let { mClusVM.getProductsByCluster(it) }
        }
        clusterId?.let {
            mClusVM.getClusterProdListMutableData(it)
                .observe(viewLifecycleOwner, Observer<RealmResults<ProductCatalogue>> {
                    mClusProductList = it
                    clusterProductAdapter?.updateProductList(mClusProductList)
                })
        }
        mBinding?.swipeRegionProducts?.isRefreshing = true
    }

    private fun setRecyclerList(){
        mBinding?.regionProdRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        clusterProductAdapter = CatalogueProductAdapter(requireContext(),
            clusterId?.let { mClusVM.getClusterProdListMutableData(it).value })
        mBinding?.regionProdRecyclerList?.adapter = clusterProductAdapter
        clusterProductAdapter?.enqListener = this
    }

    private fun setFilterList(){
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
                    mFilteredList = ProductPredicates.getFilteredClusterProducts(clusterId,filterBy)
                    clusterProductAdapter?.updateProductList(mFilteredList)
                    if(mFilteredList?.size == 0){
                        mBinding?.emptyView?.visibility = View.VISIBLE
                        mBinding?.regionProdRecyclerList?.visibility = View.GONE
                    }else{
                        mBinding?.emptyView?.visibility = View.GONE
                        mBinding?.regionProdRecyclerList?.visibility = View.VISIBLE
                    }
                }
//                else{
//                    clusterProductAdapter?.updateProductList(mClusProductList)
//                }
            }
        })
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
        setFilterList()
    }

    override fun onSuccessEnquiryGeneration(enquiry: GenerateEnquiryResponse) {
        try {
            Handler(Looper.getMainLooper()).post {
                Log.e("EnquiryGeneration", "Onsucces")
                dialog?.dismiss()
                enqID = enquiry?.data?.enquiry?.id.toString()
                enqCode = enquiry?.data?.enquiry?.code.toString()
                sucDialog = Utility.enquiryGenSuccessDialog(requireActivity(),enqID.toString(),enqCode.toString())
                Handler().postDelayed({ sucDialog?.show() }, 500)
            }
        } catch (e: Exception) {
            Log.e("EnquiryGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onExistingEnquiryGeneration(productName: String, id: String, code:String) {
        try {
            Handler(Looper.getMainLooper()).post {
                Log.e("ExistingEnqGeneration", "Onsuccess")
                dialog?.dismiss()

                enqID = id
                enqCode = code
                prodName = productName
                exDialog = Utility.enquiryGenExistingDialog(requireActivity(),enqID.toString(), enqCode.toString(),prodName.toString())

                var btn_gen = exDialog?.findViewById(R.id.btn_ex_generate_new_enquiry) as TextView
                btn_gen?.setOnClickListener {
                    productID?.let { it1 -> mEnqVM?.generateEnquiry(it1,false,"Android") }
                    exDialog?.cancel()
                    Handler().postDelayed({ dialog?.show() }, 500)
                }
                Handler().postDelayed({ exDialog?.show() }, 500)
            }
        } catch (e: Exception) {
            Log.e("ExistingEnqGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailedEnquiryGeneration() {
        try {
            Handler(Looper.getMainLooper()).post {dialog?.dismiss()
                Log.e("EnquiryGeneration", "onFailure")
            }
        } catch (e: Exception) {dialog?.dismiss()
            Log.e("EnquiryGeneration", "Exception onFailure " + e.message)
        }
    }

    override fun onEnquiryGenClick(productId: Long, isCustom: Boolean) {

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireActivity())
        } else {
            mEnqVM.ifEnquiryExists(productId,false)
            productID = productId
            dialog?.show()
        }
    }


    companion object {
        fun newInstance() = RegionProdListFragment()
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("clusterList", "OnFailure")
                mBinding?.swipeRegionProducts?.isRefreshing = false
                clusterId?.let { mClusVM.getClusterProdListMutableData(it) }
                Utility.displayMessage("Error while fetching list", requireContext())
            }
            )
        } catch (e: Exception) {
            Log.e("clusterList", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("clusterList", "onSuccess")
                mBinding?.swipeRegionProducts?.isRefreshing = false
                clusterId?.let { mClusVM.getClusterProdListMutableData(it) }
            }
            )
        } catch (e: Exception) {
            Log.e("clusterList", "Exception onFailure " + e.message)
        }
    }


}
