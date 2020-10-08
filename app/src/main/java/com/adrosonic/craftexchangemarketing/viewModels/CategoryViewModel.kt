package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CategoryProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    interface CategoryListInterface{
        fun onFailure()
        fun onSuccess()
    }

    var catListener : CategoryListInterface?= null

    val catList : MutableLiveData<RealmResults<CategoryProducts>> by lazy { MutableLiveData<RealmResults<CategoryProducts>>() }
    val catProdList : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }


    fun getCategoryListMutableData(): MutableLiveData<RealmResults<CategoryProducts>> {
        catList.value=loadCategoryList()
        return catList
    }

    fun loadCategoryList(): RealmResults<CategoryProducts> {
        var catList = ProductPredicates.getAllCategoryProducts()
        Log.e("catList","catList :"+catList?.size)
        return catList!!
    }

    fun getCatProdListMutableData(catID : Long): MutableLiveData<RealmResults<ProductCatalogue>> {
        catProdList.value=loadCatProdList(catID)
        return catProdList
    }

    fun loadCatProdList(catID : Long): RealmResults<ProductCatalogue> {
        var catProdList = ProductPredicates.getCategoryProductsFromId(catID)
        Log.e("catProdList","catProdList :"+catProdList?.size)
        return catProdList!!
    }

    fun getAllCategories(){
        craftexchangemarketingRepository
            .getProductService()
            .getAllProducts().enqueue(object : Callback, retrofit2.Callback<AllProductsResponse> {
                override fun onFailure(call: Call<AllProductsResponse>, t: Throwable) {
                    t.printStackTrace()
                    catListener?.onFailure()
                }
                override fun onResponse(
                    call: Call<AllProductsResponse>, response: Response<AllProductsResponse>
                ) {
                    if (response.body()?.valid == true) {
                        ProductPredicates.insertAllCategoryProducts(response.body())
                        catListener?.onSuccess()
                    } else {
                        catListener?.onFailure()

                    }
                }
            })
    }

    fun getProductsByCategory(catID : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductService()
            .getProductByCategory(token, catID)
            .enqueue(object : Callback, retrofit2.Callback<CatalogueProductsResponse> {
                    override fun onFailure(call: Call<CatalogueProductsResponse>, t: Throwable) {
                        t.printStackTrace()
                        catListener?.onFailure()
                    }

                    override fun onResponse(
                        call: Call<CatalogueProductsResponse>, response: Response<CatalogueProductsResponse>
                    ) {
                        if (response.body()?.valid == true) {
                            ProductPredicates.insertProductsInCatalogue(response.body()?.data?.products)
                            catListener?.onSuccess()
                        } else {
                            catListener?.onFailure()
                        }
                    }
                })
    }
}