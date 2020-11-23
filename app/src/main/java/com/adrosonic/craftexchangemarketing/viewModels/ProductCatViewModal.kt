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
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.*
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.ownDesign.DeleteOwnProductRespons
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiry.EnquiryProductResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
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
    interface FilteredArtisanInterface{
        fun onSuccess(list:List<FilteredArtisans>?)
        fun onFailure()
    }
    interface UploadProdInterface{
        fun onUploadSuccess()
        fun onUploadFailure()
    }
    interface DeleteProdInterface{
        fun onDeleteSuccess()
        fun onDeleteFailure()
    }
    var listener: ProdInterface? = null
    var prodListener: ProductDetailsInterface? = null
    var filteredListener: FilteredArtisanInterface? = null
    var uploadProdListener: UploadProdInterface? = null
    var deletedProdListener: DeleteProdInterface? = null
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

    fun getFilteredArtisans(clusterId: Int,searchStr:String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getProductCatService()
            .getFilteredArtisans(token,clusterId,searchStr)
            .enqueue(object: Callback, retrofit2.Callback<FilteredArtisanResponse> {
                override fun onFailure(call: Call<FilteredArtisanResponse>, t: Throwable) {
                    t.printStackTrace()
                    filteredListener?.onFailure()
                    Log.e("getFilteredArtisans","onFailure: "+t.message)
                }
                override fun onResponse(
                    call: Call<FilteredArtisanResponse>,
                    response: retrofit2.Response<FilteredArtisanResponse>) {
                    if(response.body()?.valid == true){
                        Log.e("getFilteredArtisans","onSuccess: ${response?.body()}")
                        filteredListener?.onSuccess(response.body()?.data)

                    }else{
                        filteredListener?.onFailure()
                        Log.e("ArtisanProduct","onFailure: "+response.body()?.errorCode)
                    }
                }

            })
    }

    fun uploadProduct(productData:String,imageList:ArrayList<String>?,artisanId:Int){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("Offline","productData :"+productData)
        Log.e("Offline","artisanId :"+artisanId)

        var dataLength=0L
        imageList?.forEach {
            dataLength=dataLength+ File(it).length()
        }
        var boundary= UUID.randomUUID().toString()
        var headerBoundary="multipart/form-data;boundary="+boundary

        val byteData=prepareMultiPartBody(boundary,dataLength,imageList)
        Log.e("Offline","prepareMultiPartBody 666666 "+byteData?.capacity())
        val body = byteData!!.array().toRequestBody(MediaType.parse("image/*"), 0, byteData.capacity())
        Log.e("Offline","prepareMultiPartBody 77777 "+body.contentLength())
        val bodyMultipart = MultipartBody.Builder()
            .addPart(body)
            .build()
        Log.e("Offline","prepareMultiPartBody 88888 "+bodyMultipart.boundary)
        craftexchangemarketingRepository.getProductCatService()
            .uploadProductTemplate(token,headerBoundary,dataLength, productData,artisanId, bodyMultipart)
            .enqueue(object : Callback, retrofit2.Callback<ArtisanProductTemplateRespons> {
                override fun onFailure(
                    call: Call<ArtisanProductTemplateRespons>,
                    t: Throwable
                ) {
                    uploadProdListener?.onUploadFailure()
                    t.printStackTrace()
                    Log.e("Offline", "getProductUploadData onFailure: " + t.localizedMessage)
                }

                override fun onResponse(
                    call: Call<ArtisanProductTemplateRespons>,
                    response: retrofit2.Response<ArtisanProductTemplateRespons>
                ) {
                    Log.e("Offline", "onResponse : ${response.isSuccessful} and ${response.message()}")
                    if (response.body()?.valid == true) {
//                        deleteOfflineEntries(prodId)
                        Log.e("Offline", "ifffff : ${response.body()?.data?.clusterName}")
                        Log.e("Offline", "ifffff : ${response.body()?.data?.brand}")
                        uploadProdListener?.onUploadSuccess()
                    }else uploadProdListener?.onUploadFailure()
                }
            })

    }
    private
    fun prepareMultiPartBody(
        boundary:String,
        dataLength:Long,
        imageList:ArrayList<String>?
    ): ByteBuffer? {
        var ctr=0
        var body = ByteBuffer.allocate(dataLength.toInt()+5000)
        Log.e("Offline","prepareMultiPartBody 1111111")
        var boundaryPrefix = "--$boundary\n"
        Log.e("Offline","prepareMultiPartBody 2222222")
        imageList?.forEach {
            ctr++
            body.put(boundaryPrefix.toByteArray(StandardCharsets.UTF_8))
            val file = File(it)
//            Content-Disposition: form-data; name="file2"; filename="Screenshot_20200801-115847.png"
//            Content-Type: image/png
            var contentDisposition = "Content-Disposition: form-data; name=file$ctr; filename=${file.name}\r\n"
            body.put(contentDisposition.toByteArray(StandardCharsets.UTF_8))
            var mimetype= MediaType.parse("image/*")
            var mimeType = "Content-Type: $mimetype\r\n\r\n"
            body.put(mimeType.toByteArray(StandardCharsets.UTF_8))
            Log.e("Offline","prepareMultiPartBody 3333 "+file.name)
            Log.e("Offline","prepareMultiPartBody 3333 "+file.length())
            body.put(file.readBytes())
            Log.e("Offline","prepareMultiPartBody 4444 "+ctr)
            body.put("\r\n".toByteArray(StandardCharsets.UTF_8))
            Log.e("Offline","prepareMultiPartBody 5555")
            if(ctr==imageList.size){
                var bottomBoundaryStr = "--$boundary--"
                body.put(bottomBoundaryStr.toByteArray(StandardCharsets.UTF_8))
            }
        }

        return body
    }

    fun editProduct(productData:String,imageList:ArrayList<String>?){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("Offline","productData :"+productData)
        var dataLength=0L
        imageList?.forEach {
            dataLength=dataLength+ File(it).length()
        }
        var boundary= UUID.randomUUID().toString()
        var headerBoundary="multipart/form-data;boundary="+boundary

        val byteData=prepareMultiPartBody(boundary,dataLength,imageList)
        Log.e("Offline","prepareMultiPartBody 666666 "+byteData?.capacity())
        val body = byteData!!.array().toRequestBody(MediaType.parse("image/*"), 0, byteData.capacity())
        Log.e("Offline","prepareMultiPartBody 77777 "+body.contentLength())
        val bodyMultipart = MultipartBody.Builder()
            .addPart(body)
            .build()
        Log.e("Offline","prepareMultiPartBody 88888 "+bodyMultipart.boundary)

        craftexchangemarketingRepository.getProductCatService()
            .editProductTemplate(token,headerBoundary,dataLength, productData, bodyMultipart)
            .enqueue(object : Callback, retrofit2.Callback<EditProductCatResponse> {
                override fun onFailure(
                    call: Call<EditProductCatResponse>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    uploadProdListener?.onUploadFailure()
                    Log.e("Offline", "getProductUploadData onFailure: " + t.localizedMessage)
                }

                override fun onResponse(
                    call: Call<EditProductCatResponse>,
                    response: retrofit2.Response<EditProductCatResponse>
                ) {
                    Log.e("Offline", "onResponse prod template : ${response.body()?.valid}")
                    if (response.body()?.valid == true) {
                        uploadProdListener?.onUploadSuccess()
                        Log.e("Offline", "iff  : ${response.body()?.data?.brand}")
                        Log.e("Offline", "iff  : ${response.body()?.data?.clusterName}")
                    } else
                    {
                        Log.e("Offline", "else  : ${response.body()?.data?.brand}")
                        uploadProdListener?.onUploadFailure()
                    }
                }
            })

    }

    fun deleteProduct( prodId: Long?) {
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN, "")}"
        Log.e("Offline", "deleteProduct prodId :" + prodId)
        craftexchangemarketingRepository.getProductCatService()
            .deleteProducts(token,prodId!!.toInt())
            .enqueue(object: Callback, retrofit2.Callback<DeleteOwnProductRespons?> {
                override fun onFailure(call: Call<DeleteOwnProductRespons?>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("deleteProduct","onFailure: "+t.message)
                    deletedProdListener?.onDeleteFailure()
                }
                override fun onResponse(
                    call: Call<DeleteOwnProductRespons?>,
                    response: retrofit2.Response<DeleteOwnProductRespons?>) {
                    val res=response.body()
                    Log.e("deleteProduct", "onResponse:1111 " + res?.valid)
                    if(res!=null) {
                        Log.e("deleteProduct", "onResponse:2222 " + res?.data)
                        if (res.valid) {
                            Log.e("deleteProduct", "onResponse:333 " + res?.data)
                            deletedProdListener?.onDeleteSuccess()
                           ProductCataloguePredicates.deleteProductEntry(prodId)
                        }else deletedProdListener?.onDeleteFailure()
                    }else deletedProdListener?.onDeleteFailure()
                }

            })
    }

}