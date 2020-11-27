package com.adrosonic.craftexchangemarketing.ui.modules.products

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.EnquiryProductDetails
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentViewProductDetailsBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.ProductCare
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog.addProduct.addAdminProductIntent
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.productDetails.*
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.ProductCatViewModal
import com.adrosonic.craftexchangemarketing.viewModels.ViewProductsViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.synnapps.carouselview.ImageListener


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ViewProductDetailsFragment : Fragment(),
ProductCatViewModal.ProductDetailsInterface{
     var mBinding : FragmentViewProductDetailsBinding?= null

    var productID : Long ?= 0
    var isAntran : Boolean ?= false
    var jsonProductData : String ?=""
    var productUploadData : ProductUploadData?= null
    private var mUserConfig = UserConfig()

    var careSelctionList = ArrayList<Pair<Long,String>>()
    var mCare = mutableListOf<ProductCare>()
    var prodCareAdapter : ProductCareRecyclerAdapter?= null
    var weaveSelctionList = ArrayList<Pair<Long,String>>()
    var url : String?= ""

    private var productDetails : EnquiryProductDetails?= null

    val mViewModel : ProductCatViewModal by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_product_details, container, false)
        arguments?.let {
            productID = it.getLong(ARG_PARAM1)
            isAntran = it.getBoolean(ARG_PARAM2)
        }

        jsonProductData = mUserConfig.productUploadJson.toString()
        val gson = GsonBuilder().create()
        productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel?.prodListener = this

         if(Utility.checkIfInternetConnected(requireActivity())){
               productID?.let { mViewModel?.getArtisanProduct(it) }
               viewLoader()
         }else{
              Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
         }
        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }
        mBinding?.imgEditProduct?.setOnClickListener {
            productDetails?.productId?.let {  context?.startActivity(context?.addAdminProductIntent(it))}
        }
        if(isAntran!!)mBinding?.imgEditProduct?.visibility=View.VISIBLE
        else mBinding?.imgEditProduct?.visibility=View.GONE
    }

    fun viewLoader(){
//        mBinding?.productDetails?.visibility = View.GONE
        mBinding?.progressLayout?.visibility = View.VISIBLE
    }

    fun hideLoader(){
//        mBinding?.productDetails?.visibility = View.VISIBLE
        mBinding?.progressLayout?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        if(Utility.checkIfInternetConnected(requireActivity())){
            productID?.let { mViewModel?.getArtisanProduct(it) }
            viewLoader()
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Product Details", "onFailure")
//                isCustomProduct?.let { productID?.let { it1 -> mViewModel.getEnqProductDetails(it1, it) } }
                hideLoader()

            })
        } catch (e: Exception) {
            Log.e("Product Details", "Exception onFailure " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Product Details", "onSuccess")
                productDetails =  productID?.let { it1 -> mViewModel.getEnqProductDetails(it1) } ?.value
                setDetails()
                hideLoader()

            })
        } catch (e: Exception) {
            Log.e("Product Details", "Exception onFailure " + e.message)
        }
    }

    fun setDetails(){
        Handler(Looper.getMainLooper()).post(Runnable {
        Log.e("ViewEnqProd","Product : $productDetails")

        //ProductImage
        getProductImages(productDetails?.productId)

        //ProductName &Description
        mBinding?.productTitle?.text = productDetails?.productTag ?: "N.A"
        mBinding?.productDescription?.text = productDetails?.product_spe ?: "N.A"

        //Cluster & Category
        mBinding?.regionName?.text = productDetails?.clusterName ?: "N.A"
        mBinding?.categoryName?.text = productDetails?.productCategoryName ?: "N.A"

        //Weave Details
        getWeaveTypes()
        getWeavesusedDetails(productDetails)

        //Care details
        prodCareAdapter = ProductCareRecyclerAdapter(requireActivity(),mCare)
        getProductCares(productDetails?.productId)

        //product cloth details
         mBinding?.reedCountValue?.text = productDetails?.reedCount ?: "N.A"
        //TODO weight and dimensions in list view
        if(productDetails?.productCategoryName == "Fabric") {
            mBinding?.gsmDetails?.visibility = View.VISIBLE
            mBinding?.gsmValue?.text = productDetails?.gsm ?: "N.A"
        }else{
            mBinding?.gsmDetails?.visibility = View.GONE
        }
        productTypeName = productDetails?.productTypeDesc
        var productWeight = productDetails?.weight ?: "N.A"
        mBinding?.weightValue?.text = "$productTypeName\t\t$productWeight"

        setDimensions(productDetails)
        })
    }

    fun getProductImages(productId : Long?){
        var imageList = ProductPredicates.getAllImagesOfProduct(productId)
        var size = imageList?.size

        imageUrlList.clear()
        if (imageList != null) {
            for (size in imageList){
                var imagename = size?.imageName
//                if(isAntran == true){
//                    url = Utility.getCustomProductImagesUrl(productDetails?.productId, imagename)
//                }else{
                    url = Utility.getProductsImagesUrl(productDetails?.productId, imagename)
//                }
                imageUrlList.add(url!!)
            }
            if(imageUrlList !=null) {
                var imageListener = ImageListener { position, imageView ->
                    imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                    ImageSetter.setImage(requireActivity(), imageUrlList[position],imageView)
                }
                mBinding?.carouselViewProducts?.setImageListener(imageListener)
                mBinding?.carouselViewProducts?.pageCount = imageUrlList.size

                mBinding?.carouselViewProducts?.setImageClickListener {
                    UserConfig.shared.imageUrlList= Gson().toJson(imageUrlList)
                    startActivity(requireActivity().fullScreenImageIntent())
                }
            }
        }
    }


    fun getWeaveTypes(){
        var list = ProductPredicates.getWeaveTypesOfProduct(productDetails?.productId)

        var weaveType = productUploadData?.data?.weaves
        weaveSelctionList.clear()
        weaveType?.forEach { weaveSelctionList.add( Pair(it.id ,it.weaveDesc) ) }

        var weaveList = arrayListOf<String>()
        if (list != null) {
            for(i in list){
                var id = i.weaveId
                weaveSelctionList.find { it.first == id }?.second?.let { weaveList.add(it) }
            }
            var adapter= ArrayAdapter<String>(requireActivity(), R.layout.item_weave_list,weaveList)
            mBinding?.weaveTypeList?.adapter = adapter
        }
    }

    fun getWeavesusedDetails(details : EnquiryProductDetails?){
        mBinding?.warpYarnValue?.text = details?.warpYarnDesc ?: "N.A"
        mBinding?.warpYarnCountValue?.text = details?.warpYarnCount ?: "N.A"
        mBinding?.warpDyeValue?.text = details?.warpDyeDesc ?: "N.A"

        mBinding?.weftYarnValue?.text = details?.weftYarnDesc ?: "N.A"
        mBinding?.weftYarnCountValue?.text = details?.weftYarnCount ?: "N.A"
        mBinding?.weftDyeValue?.text = details?.weftDyeDesc ?: "N.A"

        mBinding?.extraweftYarnValue?.text = details?.extraWeftYarnDesc ?: "N.A"
        mBinding?.extraweftYarnCountValue?.text = details?.extraWeftYarnCount ?: "N.A"
        mBinding?.extraweftDyeValue?.text = details?.extraWeftDyeDesc ?: "N.A"
    }

    fun getProductCares(productId : Long?){
        var list = ProductPredicates.getWashCareInstrctionsOfProduct(productId)
        var productCare = productUploadData?.data?.productCare
        mCare.clear()
        careSelctionList.clear()
        productCare?.forEach { careSelctionList.add( Pair(it.id ,it.productCareDesc) )  }
        if (list != null) {
            for(i in list){
                var id = i.productCareId
                careSelctionList.forEach {
                    if(it.first == id){
                        mCare.add(ProductCare(it.first,it.second))
                    }
                }
            }
            Log.e("ViewEnqProd"," Care List : $mCare")
            setupProductCareRecycler()
        }
    }

    private fun setupProductCareRecycler(){
        mBinding?.washCareList?.adapter = prodCareAdapter
        mBinding?.washCareList?.layoutManager = LinearLayoutManager(requireActivity(),
            LinearLayoutManager.VERTICAL, false)
        prodCareAdapter?.notifyDataSetChanged()
    }

    fun setDimensions(details: EnquiryProductDetails?){
        var l = SpannableString("L")
        l.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(),R.color.length_unit_color)), 0, l.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        var w = SpannableString("W")
        w.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(),R.color.swipe_background)), 0, w.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding?.dimensUnitText?.text = l
        mBinding?.dimensUnitText?.append("\tX\t")
        mBinding?.dimensUnitText?.append(w)

        var length = SpannableString(details?.productLength)
        length.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(),R.color.length_unit_color)), 0, length.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        var width = SpannableString(details?.productWidth)
        width.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(),R.color.swipe_background)), 0, width.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        var dimensions = "$productTypeName\t"
        mBinding?.prodDimensValue?.text = dimensions
        mBinding?.prodDimensValue?.append(length)
        mBinding?.prodDimensValue?.append("\tX\t")
        mBinding?.prodDimensValue?.append(width)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: Long, param2: Boolean) =
            ViewProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, param1)
                    putBoolean(ARG_PARAM2, param2)
                }
            }
    }
}