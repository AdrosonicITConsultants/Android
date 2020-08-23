package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.CategoryProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.database.predicates.ClusterPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.AllProductsResponse
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

    fun getCategoryListMutableData(): MutableLiveData<RealmResults<CategoryProducts>> {
        catList.value=loadCategoryList()
        return catList
    }

    fun loadCategoryList(): RealmResults<CategoryProducts> {
        var catList = ProductPredicates.getAllCategoryProducts()
        Log.e("catList","catList :"+catList?.size)
        return catList!!
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
}