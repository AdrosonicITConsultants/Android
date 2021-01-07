package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.CategoryProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ClusterPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.AllProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
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

    fun getCatProdListMutableData(catID : Long,madeWithAnt:Long): MutableLiveData<RealmResults<ProductCatalogue>> {
        catProdList.value=loadCatProdList(catID,madeWithAnt)
        return catProdList
    }

    fun loadCatProdList(catID : Long,madeWithAnt : Long): RealmResults<ProductCatalogue> {
        var catProdList = ProductPredicates.getCategoryProductsFromId(catID,madeWithAnt)
        Log.e("catProdList","catProdList :"+catProdList?.size)
        return catProdList!!
    }

    fun getAllCategories(){
        CraftExchangeRepository
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
        CraftExchangeRepository
            .getProductService()
            .getProductByCategory(catID)
            .enqueue(object : Callback, retrofit2.Callback<CatalogueProductsResponse> {
                    override fun onFailure(call: Call<CatalogueProductsResponse>, t: Throwable) {
                        t.printStackTrace()
                        catListener?.onFailure()
                    }

                    override fun onResponse(
                        call: Call<CatalogueProductsResponse>, response: Response<CatalogueProductsResponse>
                    ) {
                        if (response.body()?.valid == true) {
                            ProductPredicates.insertProductsInCatalogue(response.body()?.data?.products,0)
                            if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false))getProductsInWishlist()
                            catListener?.onSuccess()
                        } else {
                            catListener?.onFailure()
                        }
                    }
                })
    }

    fun getProductsInWishlist(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getWishlistService()
            .getProductsInWishlist(token)
            .enqueue(object: Callback, retrofit2.Callback<CatalogueProductsResponse> {
                override fun onFailure(call: Call<CatalogueProductsResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("LandingViewModel","wishlist onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<CatalogueProductsResponse>,
                    response: Response<CatalogueProductsResponse>) {

                    if(response.body()?.valid == true){
                        val response=response.body()?.data
                        ProductPredicates.insertProductsInCatalogue(response?.products,1)
                        catListener?.onSuccess()
                    }
                }
            })
    }
}