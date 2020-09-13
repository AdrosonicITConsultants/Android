package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.EnquiryProductDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.GetAllOwnDesignResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryProductResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import javax.security.auth.callback.Callback

class ViewProductsViewModel(application: Application) : AndroidViewModel(application)  {

    interface ViewProductsInterface{
        fun onSuccess()
        fun onFailure()
    }
    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
    var listener: ViewProductsInterface? = null

    private val productData : MutableLiveData<EnquiryProductDetails> by lazy { MutableLiveData<EnquiryProductDetails>() }

    fun getEnqProductDetails(productId : Long, isCustom : Boolean): MutableLiveData<EnquiryProductDetails> {
        productData.value = loadEnqProductData(productId,isCustom)
        return productData
    }

    private fun loadEnqProductData(productId : Long, isCustom : Boolean): EnquiryProductDetails {
        var product= EnquiryPredicates.getEnqProduct(productId,isCustom)
        return product!!
    }

    fun getBuyerCustomProduct(productId : Long) {
        CraftExchangeRepository
            .getBuyerOwnDesignService()
            .getSingleOwnDesignProduct(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryProductResponse> {
                override fun onFailure(call: Call<EnquiryProductResponse>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailure()
                    Log.e("BuyerCustomProduct","onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryProductResponse>,
                    response: retrofit2.Response<EnquiryProductResponse>) {

                    if(response.body()?.valid == true){
                        listener?.onSuccess()
                        Log.e("BuyerCustomProduct","onSuccess: ")
                        EnquiryPredicates.insertEnquiryProduct(response?.body()!!,true)
                    }else{
                        listener?.onFailure()
                        Log.e("BuyerCustomProduct","onFailure: "+response.body()?.errorCode)

                    }
                }

            })
    }

    fun getArtisanProduct(productId: Long){
        CraftExchangeRepository
            .getProductService()
            .getSingleProduct(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<EnquiryProductResponse> {
                override fun onFailure(call: Call<EnquiryProductResponse>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailure()
                    Log.e("Artisan Product","onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<EnquiryProductResponse>,
                    response: retrofit2.Response<EnquiryProductResponse>) {
                    if(response.body()?.valid == true){
                        listener?.onSuccess()
                        Log.e("Artisan Product","onSuccess: ")
                        EnquiryPredicates.insertEnquiryProduct(response?.body()!!,false)
                    }else{
                        listener?.onFailure()
                        Log.e("Artisan Product","onFailure: "+response.body()?.errorCode)
                    }
                }

            })
    }
}