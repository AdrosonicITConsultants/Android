package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import io.realm.RealmResults

class ProductCatalogueViewModel(application: Application) : AndroidViewModel(application)  {
    interface getProductsInCatalogueInterface{
        fun onSuccess()
        fun onFailure()
    }

    var listener : ArtisanProductsViewModel.productsFetchInterface?= null
    val productListData : MutableLiveData<RealmResults<ProductCatalogue>> by lazy { MutableLiveData<RealmResults<ProductCatalogue>>() }

//    fun getCatalogueProductListMutableData(): MutableLiveData<RealmResults<ProductCatalogue>> {
//        productListData.value=loadCatalogueProductListData()
//        return productListData
//    }
//
//    fun loadCatalogueProductListData(): RealmResults<ProductCatalogue> {
//        var productList= ProductPredicates.getA
//        Log.e("Wishlist","loadwishListData :"+productList?.size)
//        return productList!!
//    }
}