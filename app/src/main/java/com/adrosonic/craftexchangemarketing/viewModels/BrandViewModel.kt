package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.BrandList
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.BrandListResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class BrandViewModel(application: Application) : AndroidViewModel(application) {

    interface BrandListInterface{
        fun onFailure()
        fun onSuccess()
    }

    var brandListener : BrandListInterface?= null

    val allBrandsList : MutableLiveData<RealmResults<BrandList>> by lazy {MutableLiveData<RealmResults<BrandList>>()}
    val brandProdList : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }


    fun getBrandListMutableData(): MutableLiveData<RealmResults<BrandList>> {
        allBrandsList.value=loadBrandList()
        return allBrandsList
    }

    fun loadBrandList(): RealmResults<BrandList> {
        var brandList = ProductPredicates.getAllBrandDetails()
        Log.e("BrandList","BrandList :"+brandList?.size)
        return brandList!!
    }

    fun getBrandProdListMutableData(artisanID : Long): MutableLiveData<RealmResults<ProductCatalogue>> {
        brandProdList.value=loadBrandProdList(artisanID)
        return brandProdList
    }

    fun loadBrandProdList(artisanID : Long): RealmResults<ProductCatalogue> {
        var brandProdList = ProductPredicates.getBrandProductsFromId(artisanID)
        Log.e("brandProdList","brandProdList :"+brandProdList?.size)
        return brandProdList!!
    }

    fun getAllBrands(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductService()
            .getFilteredArtisans(token).enqueue(object : Callback, retrofit2.Callback<BrandListResponse> {
                override fun onFailure(call: Call<BrandListResponse>, t: Throwable) {
                    t.printStackTrace()
                    brandListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<BrandListResponse>,
                    response: Response<BrandListResponse>
                ) {
                    if (response.body()?.valid == true) {
                        ProductPredicates.insertBrands(response.body())
                        brandListener?.onSuccess()
                    } else {
                        brandListener?.onFailure()                    }
                }
            })
    }

    fun getProductsByArtisan(artisanID : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductService()
            .getProductsByArtisan(token,artisanID)
            .enqueue(object : Callback, retrofit2.Callback<CatalogueProductsResponse> {
                override fun onFailure(call: Call<CatalogueProductsResponse>, t: Throwable) {
                    t.printStackTrace()
                    brandListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<CatalogueProductsResponse>, response: Response<CatalogueProductsResponse>
                ) {
                    if (response.body()?.valid == true) {
                        ProductPredicates.insertProductsInCatalogue(response.body()?.data?.products)
                        brandListener?.onSuccess()
                    } else {
                        brandListener?.onFailure()
                    }
                }
            })
    }
}