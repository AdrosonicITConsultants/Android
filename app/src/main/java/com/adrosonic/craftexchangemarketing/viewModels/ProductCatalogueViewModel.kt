package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductImages
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.WishlistPredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ProductCatalogueViewModel(application: Application) : AndroidViewModel(application)  {

    interface CatalogueProductInterface{
        fun onProdFetchSucc()
        fun onProdFetchFail()
    }

    var listener : CatalogueProductInterface?= null
    private val productData : MutableLiveData<ProductCatalogue> by lazy { MutableLiveData<ProductCatalogue>() }
    private val imageData : MutableLiveData<RealmResults<ProductImages>> by lazy {MutableLiveData<RealmResults<ProductImages>>()}

    fun getCatalogueProductMutableData(productId : Long): MutableLiveData<ProductCatalogue> {
        productData.value = loadCatalogueProductData(productId)
        return productData
    }

    private fun loadCatalogueProductData(productId : Long): ProductCatalogue {
        var product= ProductPredicates.getProductDetails(productId)
        return product!!
    }

    fun getProductImagesMutableData(productId : Long): MutableLiveData<RealmResults<ProductImages>> {
        imageData.value = loadProductImages(productId)
        return imageData
    }

    private fun loadProductImages(productId : Long): RealmResults<ProductImages>? {
        return ProductPredicates.getAllImagesOfProduct(productId)
    }

    fun getProductDetailsById(productId : Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getWishlistService()
            .getSingleProductDetails(token, productId.toInt())
            .enqueue(object : Callback, retrofit2.Callback<SingleProductDetails> {
                override fun onFailure(call: Call<SingleProductDetails>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("prodDetails","Failure : "+t.printStackTrace())
                    listener?.onProdFetchFail()
                }
                override fun onResponse(
                    call: Call<SingleProductDetails>, response: Response<SingleProductDetails>
                ) {
                    if (response.body()?.valid == true) {
                        response?.body()?.data?.let { WishlistPredicates?.insertSingleProduct(it) }
                        listener?.onProdFetchSucc()

                    } else {
                        Log.e("prodDetails","Failure")
                        listener?.onProdFetchFail()

                    }
                }
            })
    }

}