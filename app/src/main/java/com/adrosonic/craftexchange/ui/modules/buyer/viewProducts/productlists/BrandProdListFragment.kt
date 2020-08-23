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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentBrandProdListBinding
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter.CatalogueProductsAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.BrandViewModel
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.dialog_gen_enquiry_update_or_new.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandProdListFragment : Fragment(),
    EnquiryViewModel.GenerateEnquiryInterface,
    BrandViewModel.BrandListInterface,
    CatalogueProductsAdapter.EnquiryGeneratedListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandProdListBinding ?= null
    var brandProductAdapter : CatalogueProductsAdapter?= null

    private var mSpinner = mutableListOf<String>()

    private var mBrandProdList : RealmResults<ProductCatalogue> ?= null
    private var mFilteredList : RealmResults<ProductCatalogue> ?= null


    var artisanId : Long ?= 0
    var artisanName : String ?= ""
    var brandName : String ?= ""
    var url : String ?=""

    val mEnqVM : EnquiryViewModel by viewModels()
    val mBrandVM : BrandViewModel by viewModels()

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_prod_list, container, false)

        artisanId = this.requireArguments().getString(ConstantsDirectory.ARTISAN_ID)?.toLong()
        artisanName = this.requireArguments().getString(ConstantsDirectory.ARTISAN)
        brandName = this.requireArguments().getString(ConstantsDirectory.COMP_NAME)
        var logo = this.requireArguments().getString(ConstantsDirectory.BRAND_IMG_NAME)
        var photo = this.requireArguments().getString(ConstantsDirectory.PROFILE_PHOTO_NAME)
        if(logo != null){
            url = Utility.getBrandLogoUrl(artisanId,logo)
        }else{
            url =  Utility.getBrandLogoUrl(artisanId,photo)
        }

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEnqVM.listener = this
        mBrandVM.brandListener = this

        brandProductAdapter?.enqListener = this

        setRecyclerList()
        setFilterList()
        mBinding?.swipeBrandProducts?.isEnabled = false

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            artisanId?.let { mBrandVM.getProductsByArtisan(it) }
        }
        artisanId?.let {
            mBrandVM.getBrandProdListMutableData(it)
                .observe(viewLifecycleOwner, Observer<RealmResults<ProductCatalogue>> {
                    mBrandProdList = it
                    brandProductAdapter?.updateProductList(mBrandProdList)
                })
        }
        mBinding?.swipeBrandProducts?.isRefreshing = true


        dialog = Utility.enquiryGenProgressDialog(requireContext())
        ImageSetter.setImage(requireContext(),url!!,mBinding?.logo!!,R.drawable.artisan_logo_placeholder
            ,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        mBinding?.brandDesc?.text = "By $artisanName"
        mBinding?.artisanBrand?.text = brandName ?: artisanName
    }

    private fun setRecyclerList(){
        mBinding?.brandProdRecyclerList?.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        brandProductAdapter = CatalogueProductsAdapter(requireContext(),
            artisanId?.let { mBrandVM.getBrandProdListMutableData(it).value })
        mBinding?.brandProdRecyclerList?.adapter = brandProductAdapter
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
       filterSpinner(requireContext(),mSpinner,mBinding?.filterBrand)
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
                    mFilteredList = ProductPredicates.getFilteredBrandProducts(artisanId,filterBy)
                    brandProductAdapter?.updateProductList(mFilteredList)

                    if(mFilteredList?.size == 0){
                        mBinding?.emptyView?.visibility = View.VISIBLE
                        mBinding?.brandProdRecyclerList?.visibility = View.GONE
                    }else{
                        mBinding?.emptyView?.visibility = View.GONE
                        mBinding?.brandProdRecyclerList?.visibility = View.VISIBLE
                    }
                }else{
                    brandProductAdapter?.updateProductList(mBrandProdList)
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
                    productID?.let { it1 -> mEnqVM.generateEnquiry(it1,false,"Android") }
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
        fun newInstance() = BrandProdListFragment()
        const val TAG = "BrandProducts"
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("brandProdList", "OnFailure")
                mBinding?.swipeBrandProducts?.isRefreshing = false
                artisanId?.let { mBrandVM.getBrandProdListMutableData(it) }
                Utility.displayMessage(
                    "Error while fetching list",
                    requireContext()
                )
            }
            )
        } catch (e: Exception) {
            Log.e("brandProdList", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("brandProdList", "onSuccess")
                mBinding?.swipeBrandProducts?.isRefreshing = false
                artisanId?.let { mBrandVM.getBrandProdListMutableData(it) }
            }
            )
        } catch (e: Exception) {
            Log.e("brandProdList", "Exception onFailure " + e.message)
        }
    }

}
