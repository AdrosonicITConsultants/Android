package com.adrosonic.craftexchange.ui.modules.buyer.productDetails

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ActivityCatalogueProductDetailsBinding
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.ProductCare
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.ProductImage
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.GsonBuilder
import kotlin.collections.ArrayList

fun Context.catalogueProductDetailsIntent(): Intent {
    return Intent(this, CatalogueProductDetailsActivity::class.java)
        .apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}
private var mBinding : ActivityCatalogueProductDetailsBinding ?= null
var imageUrlList : MutableList<String> ?= null
var productId : Long ?= 0
var productDetails : ProductCatalogue ?= null
var productUploadData : ProductUploadData?= null
private var mUserConfig = UserConfig()
var jsonProductData : String ?=""
var careSelctionList = ArrayList<Pair<Long,String>>()
var mCare = mutableListOf<ProductCare>()
var prodCareAdapter : ProductCareRecyclerAdapter?= null
var weaveSelctionList = ArrayList<Pair<Long,String>>()
var mMoreProductList = mutableListOf<ProductImage>()
var moreProdAdapter : MoreProductsRecyclerAdapter?= null
var productTypeName : String?=""

class CatalogueProductDetailsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityCatalogueProductDetailsBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        jsonProductData = mUserConfig?.productUploadJson.toString()
        val gson = GsonBuilder().create()
        productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)

        productId = intent.getStringExtra(ConstantsDirectory.PRODUCT_ID).toLong()
        getProductDetails(productId)

        getProductImages(productId)

//        var imageListener =ImageListener { position, imageView ->
//                imageUrlList?.get(position)?.let {
//                    ImageSetter.setImage(applicationContext,
//                        it,imageView)
//                }
//            }
//
//        mBinding?.carouselViewProducts?.setImageListener(imageListener)
//        imageUrlList?.count()?.let { mBinding?.carouselViewProducts?.setPageCount(it) }

        mBinding?.productTitle?.text = productDetails?.productTag ?: "-"
        mBinding?.productDescription?.text = productDetails?.product_spe ?: "-"
//        mBinding?.productNote?.text = TODO implment later

        var status : String ?= ""
        when(productDetails?.productStatusId){
            2.toLong() -> {
                status = this.getString(R.string.in_stock)
                mBinding?.productAvailabilityText?.text = status
                this?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green)
                }?.let {  mBinding?.productAvailabilityText?.setTextColor(it) }
            }
            1.toLong() -> {
                status = this?.getString(R.string.exclusively)
                var mto = SpannableString(ConstantsDirectory.MADE_TO_ORDER)
                mto.setSpan(this?.let { ContextCompat.getColor(it, R.color.light_green) }?.let {
                    ForegroundColorSpan(
                        it
                    )
                }, 0, mto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                mBinding?.productAvailabilityText?.text = status
                mBinding?.productAvailabilityText?.append(mto)
                this?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta)
                }?.let { mBinding?.productAvailabilityText?.setTextColor(it) }
            }
        }

        mBinding?.regionName?.text = productDetails?.clusterName ?: "-"
        mBinding?.categoryName?.text = productDetails?.productCategoryName ?: "-"

        setBrandDetails(productDetails?.artisanId)

        getWeavesusedDetails(productDetails)

        getWeaveTypes(productId)

        prodCareAdapter = ProductCareRecyclerAdapter(applicationContext,mCare)
        getProductCares(productId)

        mBinding?.reedCountValue?.text = productDetails?.reedCount ?: "-"
        //TODO weight and dimensions in list view

        mBinding?.gsmValue?.text = productDetails?.gsm ?: "-"

        productTypeName = productDetails?.productTypeDesc
        var productWeight = productDetails?.weight ?: "-"


        mBinding?.weightValue?.text = "$productTypeName\t\t\t$productWeight"

        setDimensions(productDetails)

        mBinding?.moreProductsText?.text = "More ${productDetails?.productCategoryName} from ${productDetails?.clusterName}..."

        moreProdAdapter = MoreProductsRecyclerAdapter(applicationContext, mMoreProductList)
        setMoreProducts(productDetails)

        mBinding?.seeAllProdText?.setOnClickListener{
            focusOnView()
        }
        mBinding?.btnBack?.setOnClickListener {
            onBackPressed()
        }
    }

    fun getProductDetails(productId : Long?){
        productDetails = ProductPredicates.getProductDetails(productId)
    }

    fun getProductImages(productId : Long?){
        var imageList = ProductPredicates.getAllImagesOfProduct(productId)
        var size = imageList
        imageUrlList?.clear()
        if (imageList != null) {
            for (size in imageList){
                Log.i("Stat","$size")
                var imagename = size?.imageName
                var url = Utility.getProductsImagesUrl(productId,imagename)
                imageUrlList?.add(url)
            }
        }
    }

    fun setBrandDetails(artisanId : Long?){
        var brand = ProductPredicates?.getBrandDetailsFromId(artisanId)
        var logo = brand?.logo
        var profile = brand?.profilePic
        var url : String ?=""
        if(logo !=null) {
            url = Utility.getBrandLogoUrl(artisanId,logo)
        }else{
            url = Utility.getProfilePhotoUrl(artisanId,profile)
        }
        mBinding?.brandLogo?.let {
            ImageSetter.setImage(applicationContext,url,
                it,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }
        mBinding?.brandName?.text = brand?.companyName ?: brand?.firstName
    }

    fun getWeavesusedDetails(details : ProductCatalogue ?){
        mBinding?.warpYarnValue?.text = details?.warpYarnDesc ?: "-"
        mBinding?.warpYarnCountValue?.text = details?.warpYarnCount ?: "-"
        mBinding?.warpDyeValue?.text = details?.warpDyeDesc ?: "-"

        mBinding?.weftYarnValue?.text = details?.weftYarnDesc ?: "-"
        mBinding?.weftYarnCountValue?.text = details?.weftYarnCount ?: "-"
        mBinding?.weftDyeValue?.text = details?.weftDyeDesc ?: "-"

        mBinding?.extraweftYarnValue?.text = details?.extraWeftYarnDesc ?: "-"
        mBinding?.extraweftYarnCountValue?.text = details?.extraWeftYarnCount ?: "-"
        mBinding?.extraweftDyeValue?.text = details?.extraWeftDyeDesc ?: "-"
    }

    fun getWeaveTypes(productId : Long?){
        var list = ProductPredicates.getWeaveTypesOfProduct(productId)

        var weaveType = productUploadData?.data?.weaves
        weaveSelctionList.clear()
        weaveType?.forEach { weaveSelctionList.add( Pair(it.id ,it.weaveDesc) ) }

        var weaveList = arrayListOf<String>()
        if (list != null) {
            for(i in list){
                var id = i.weaveId
                weaveSelctionList.find { it.first == id }?.second?.let { weaveList.add(it) }
            }
            var adapter= ArrayAdapter<String>(this, R.layout.item_weave_list,weaveList)
            mBinding?.weaveTypeList?.adapter = adapter
        }
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
//                        mCare.add(ProductCare(it.first,it.second))
                    }
                }
            }
            setupProductCareRecycler()
        }
    }

    private fun setupProductCareRecycler(){
        mBinding?.washCareList?.adapter = prodCareAdapter
        mBinding?.washCareList?.layoutManager = LinearLayoutManager(applicationContext,
            LinearLayoutManager.VERTICAL, false)
        prodCareAdapter?.notifyDataSetChanged()
    }

    fun setMoreProducts(details : ProductCatalogue ?){
        var idList = ProductPredicates.getAllProductIdsOfCategoryFromCluster(details?.productCategoryName,details?.clusterName)
        mMoreProductList.clear()
        for(i in 1..5){
            var productId = idList.random()
            var label = ProductPredicates.getProductDisplayImage(productId)
            mMoreProductList.add(ProductImage(0,label?.imageName.toString(),productId))
        }
        setupMoreProductsRecycler()
        idList.clear()
    }

    fun setDimensions(details: ProductCatalogue?){
        var length = SpannableString(details?.productLength)
        length.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext,R.color.length_unit_color)), 0, length.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        var width = SpannableString(details?.productWidth)
        width.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext,R.color.swipe_background)), 0, width.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        var dimensions = "$productTypeName\t\t"
        mBinding?.prodDimensValue?.text = dimensions
        mBinding?.prodDimensValue?.append(length)
        mBinding?.prodDimensValue?.append("\tX\t")
        mBinding?.prodDimensValue?.append(width)

//        var relProd = ProductPredicates.getRelatedProductOfProduct(details?.productId)
//        if(relProd!=null){
//            mBinding?.dividerDimens?.visibility = View.VISIBLE
//            mBinding?.relProdDimensValue?.visibility = View.VISIBLE
//
//            var length = SpannableString(relProd?.productLength)
//            length.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext,R.color.length_unit_color)), 0, length.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            var width = SpannableString(relProd?.productWidth)
//            width.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext,R.color.swipe_background)), 0, width.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            var dimensions = "${relProd?.productName}\t\t"
//            mBinding?.prodDimensValue?.text = dimensions
//            mBinding?.prodDimensValue?.append(length)
//            mBinding?.prodDimensValue?.append("\tX\t")
//            mBinding?.prodDimensValue?.append(width)
//
//        }else{
//            mBinding?.dividerDimens?.visibility = View.GONE
//            mBinding?.relProdDimensValue?.visibility = View.GONE
//        }


    }

    private fun setupMoreProductsRecycler(){
        mBinding?.moreProductImages?.adapter = moreProdAdapter
        mBinding?.moreProductImages?.layoutManager = LinearLayoutManager(applicationContext,
            LinearLayoutManager.HORIZONTAL, false)
        moreProdAdapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    private fun focusOnView() {
        Handler().post {
            mBinding?.weaveTypeUsedText?.top?.let {
                mBinding?.scrollProductDetails?.scrollTo(0,
                    it
                )
            }
        }
    }
}