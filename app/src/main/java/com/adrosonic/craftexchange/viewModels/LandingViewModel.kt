package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchange.repository.data.response.buyer.wishList.WishListedIds
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryAvaProdStageData
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryStageData
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LandingViewModel(application: Application) : AndroidViewModel(application) {


    interface wishlistFetchedInterface{
        fun onSuccess()
        fun onFailure()
    }
    var listener: wishlistFetchedInterface? = null
    val wishListData : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }

    fun getwishListMutableData(): MutableLiveData<RealmResults<ProductCatalogue>> {
        wishListData.value=loadwishListData()
        return wishListData
    }
    fun loadwishListData(): RealmResults<ProductCatalogue> {
        var wishList= WishlistPredicates.getWishListedData()
        Log.e("Wishlist","loadwishListData :"+wishList?.size)
        return wishList!!
    }

    fun logoutUser() {
        val editor = Prefs.edit()
        editor.clear()
        editor.commit()
        editor.apply()
    }

    fun getProductsOfArtisan(context : Context){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getArtisanProducts(token)
            .enqueue(object: Callback, retrofit2.Callback<ArtisanProductDetailsResponse> {
                override fun onFailure(call: Call<ArtisanProductDetailsResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<ArtisanProductDetailsResponse>,
                    response: retrofit2.Response<ArtisanProductDetailsResponse>) {
                    if(response.body()?.valid == true){
                        ProductPredicates.insertProductsOfArtisan(response.body())
                    }else{
                        Utility.displayMessage(response.body()?.errorMessage.toString(),context)
                    }
                }

            })
    }

    fun getProductUploadData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getProductUploadData(token)
            .enqueue(object: Callback, retrofit2.Callback<ProductUploadData> {
                override fun onFailure(call: Call<ProductUploadData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getProductUploadData onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<ProductUploadData>,
                    response: retrofit2.Response<ProductUploadData>) {

                    if(response.body()?.valid == true){
                        //todo store data to spf
                        Log.e("LandingViewModel","getProductUploadData :"+response.body()?.data?.productCare?.size)
                        UserConfig.shared.productUploadJson= Gson().toJson(response.body())
                        Log.e("LandingViewModel","SPF :"+UserConfig.shared.productUploadJson)

                    }else{
                        Log.e("LandingViewModel","getProductUploadData onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getEnquiryStageData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getAllEnquiryStagesData(token)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryStageData> {
                override fun onFailure(call: Call<EnquiryStageData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryStageData>,
                    response: retrofit2.Response<EnquiryStageData>) {

                    if(response.body()?.valid == true){

                        UserConfig.shared.enquiryStageData= Gson().toJson(response.body())
                        Log.e("LandingViewModel","SPF Enquiries :"+UserConfig.shared.enquiryStageData)

                    }else{
                        Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getEnquiryStageAvailableProdsData(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getEnquiryService()
            .getAvailableProdEnquiryStagesData(token)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryAvaProdStageData> {
                override fun onFailure(call: Call<EnquiryAvaProdStageData>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","getAllEnquiriesStages onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryAvaProdStageData>,
                    response: retrofit2.Response<EnquiryAvaProdStageData>) {

                    if(response.body()?.valid == true){

                        UserConfig.shared.enquiryAvaProdStageData= Gson().toJson(response.body())
                        Log.e("LandingViewModel","SPF Enquiries :"+UserConfig.shared.enquiryAvaProdStageData)

                    }else{
                        Log.e("LandingViewModel","getAvailableProdEnquiriesStages onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getwishlisteProductIds(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getWishlistService()
            .getWishlistedProductIds(token)
            .enqueue(object: Callback, retrofit2.Callback<WishListedIds> {
                override fun onFailure(call: Call<WishListedIds>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailure()
                    Log.e("LandingViewModel","wishlist onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<WishListedIds>,
                    response: retrofit2.Response<WishListedIds>) {

                    if(response.body()?.valid == true){
                        val response=response.body()?.data
                        Log.e("LandingViewModel","wishlist :"+response?.joinToString())
                        response?.forEach {
                            if(!WishlistPredicates.isProductPresent(it))   getBuyerProductDetails(it)
                        }
                        WishlistPredicates.addToWishlist(response)
                        listener?.onSuccess()

                    }else{
                        listener?.onFailure()
                        Log.e("LandingViewModel","wishlist onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getBuyerProductDetails(id:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getWishlistService()
            .getSingleProductDetails(token,id.toInt())
            .enqueue(object: Callback, retrofit2.Callback<SingleProductDetails> {
                override fun onFailure(call: Call<SingleProductDetails>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","ownproduct onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<SingleProductDetails>,
                    response: retrofit2.Response<SingleProductDetails>) {
                    if(response.body()?.valid == true){
                        val response=response.body()?.data
                        if (response != null) {
                            WishlistPredicates.insertSingleProduct(response)
                        }
                        Log.e("LandingViewModel","own product :"+response?.artistName)
                    }
                }

            })
    }

    fun getArtisanBrandDetails(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getFilteredArtisans(token).enqueue(object : Callback, retrofit2.Callback<BrandListResponse> {
                override fun onFailure(call: Call<BrandListResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<BrandListResponse>,
                    response: Response<BrandListResponse>
                ) {
                    if (response.body()?.valid == true) {
                        ProductPredicates.insertBrands(response.body())
                    }
                }
            })
    }

}