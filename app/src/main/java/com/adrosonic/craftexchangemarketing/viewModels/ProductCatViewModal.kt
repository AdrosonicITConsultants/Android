package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.AdminProductCatalogue
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.EnquiryProductDetails
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchangemarketing.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.NotificationPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.ProductCataloguePredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.CalogueRequest
import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.UserDataRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductCatalogueResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.ProductDetailsResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryProductResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.security.auth.callback.Callback

class ProductCatViewModal(application: Application) : AndroidViewModel(application) {
    interface ProductDetailsInterface{
        fun onSuccess()
        fun onFailure()
    }
    interface ProdInterface{
        fun onSuccess()
        fun onFailure()
        fun onCountSuccess(count:Long)
        fun onCountFailure()
    }
    var listener: ProdInterface? = null
    var prodListener: ProductDetailsInterface? = null
    var page=0L

    val productData : MutableLiveData<RealmResults<AdminProductCatalogue>> by lazy { MutableLiveData<RealmResults<AdminProductCatalogue>>() }
    val productDetails : MutableLiveData<EnquiryProductDetails> by lazy { MutableLiveData<EnquiryProductDetails>() }

    fun getEnqProductDetails(productId : Long): MutableLiveData<EnquiryProductDetails> {
        productDetails.value = loadEnqProductData(productId)
        return productDetails
    }

    private fun loadEnqProductData(productId : Long): EnquiryProductDetails? {
        var product= EnquiryPredicates.getEnqProduct(productId,false)
        return product
    }

    fun getProductsMutableData(isArtisan:Long,search:String,cluster: String,availability:String): MutableLiveData<RealmResults<AdminProductCatalogue>> {
        productData.value=loadProductsData(isArtisan,search,cluster,availability)
        return productData
    }
    fun loadProductsData(isArtisan:Long,search:String,cluster: String,availability:String): RealmResults<AdminProductCatalogue> {
        var products= ProductCataloguePredicates.getAllProducts(isArtisan,search,cluster,availability)
        Log.e("products","products :"+products?.size)
        return products!!
    }

    fun getArtisanProducts(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductCatService()
            .getArtisanProducts(token,null,null,null)
            .enqueue(object : Callback, retrofit2.Callback<ProductCatalogueResponse> {
                override fun onFailure(call: Call<ProductCatalogueResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ArtisanProduct","Failure :"+t.message)
                    listener?.onFailure()
                }
                override fun onResponse(
                    call: Call<ProductCatalogueResponse>,
                    response: Response<ProductCatalogueResponse>
                ) {
                    Log.e("ArtisanProduct", "outside : ${response.message()}")
                    Log.e("ArtisanProduct", "outside : ${response.errorBody()}")
                    Log.e("ArtisanProduct", "outside : ${response.body()?.data?.size}")
                    if(response.body()?.data != null) {
                        Log.e("ArtisanProduct", "Success : ")
                                Timer().schedule(object : TimerTask() {
                                    override fun run() {
                                        listener?.onSuccess()
                                    }
                                }, 1000)
                                ProductCataloguePredicates.insertProductCatalogue(response?.body()?.data!!,0)//dbcall
//                            }
                        }else{
                        Log.e("ArtisanProduct","Failure")
                        listener?.onFailure()
                    }
                }
            })
    }

    fun getDatabaseCount( availability: Long, clusterID: Long, madeWithAntaran: Long, pageNo: Long,
                          searchStr: String,sortBy: String,sortType: String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductCatService()
            .getArtisanProductsCount(token, CalogueRequest(availability,clusterID,madeWithAntaran,pageNo,searchStr,sortBy,sortType))
            .enqueue(object : Callback, retrofit2.Callback<ProductCountResponse> {
                override fun onFailure(call: Call<ProductCountResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("AdminDatabaseCount","Failure :"+t.printStackTrace().toString())
                    listener?.onCountFailure()
                }
                override fun onResponse(
                    call: Call<ProductCountResponse>,
                    response: Response<ProductCountResponse>
                ) {
                    if(response.body()?.data != null) {
                        Log.e("AdminDatabaseCount", "quotient : ${response.body()?.data?:0}")
                        listener?.onCountSuccess(response.body()?.data?:0)
                    }else{
                        Log.e("AdminDatabaseCount","Failure")
                        listener?.onCountFailure()
                    }
                }
            })
    }

    fun getArtisanProduct(productId: Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductCatService()
            .getArtisanProduct(token,productId)
            .enqueue(object: Callback, retrofit2.Callback<ProductDetailsResponse> {
                override fun onFailure(call: Call<ProductDetailsResponse>, t: Throwable) {
                    t.printStackTrace()
                    prodListener?.onFailure()
                    Log.e("ArtisanProduct","onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<ProductDetailsResponse>,
                    response: retrofit2.Response<ProductDetailsResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("ArtisanProduct","onSuccess: ${response?.body()}")
                        ProductCataloguePredicates.insertProductDetails(response.body())

                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                prodListener?.onSuccess()
                            }
                        }, 500)
                    }else{
                        prodListener?.onFailure()
                        Log.e("ArtisanProduct","onFailure: "+response.body()?.errorCode)
                    }
                }

            })
    }
}