package com.adrosonic.craftexchange.ui.modules.buyer.productDetails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.core.content.ContextCompat
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ActivityCatalogueProductDetailsBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.synnapps.carouselview.ImageListener
import io.realm.internal.Util

fun Context.catalogueProductDetailsIntent(): Intent {
    return Intent(this, CatalogueProductDetailsActivity::class.java)
//        .apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    }
}
private var mBinding : ActivityCatalogueProductDetailsBinding ?= null
var imageUrlList : MutableList<String> ?= null
var productId : Long ?= 0
var productDetails : ProductCatalogue ?= null

class CatalogueProductDetailsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityCatalogueProductDetailsBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        productId = intent.getStringExtra(ConstantsDirectory.PRODUCT_ID).toLong()
        getProductDetails(productId)
        getProductImages(productId)
        var imageListener =
            ImageListener { position, imageView ->
                imageUrlList?.get(position)?.let {
                    ImageSetter.setImage(applicationContext,
                        it,imageView)
                }
            }
        mBinding?.carouselViewProducts?.setImageListener(imageListener)
        imageUrlList?.count()?.let { mBinding?.carouselViewProducts?.setPageCount(it) }

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

        mBinding?.reedCountValue?.text = productDetails?.reedCount ?: "-"
        //TODO weight and dimensions in list view

        mBinding?.gsmValue?.text = productDetails?.gsm ?: "-"

        mBinding?.moreProductsText?.text = "More ${productDetails?.productCategoryName} from ${productDetails?.clusterName}..."

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
        var imageList = ProductPredicates.getAllProductImagesFromId(productId)
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