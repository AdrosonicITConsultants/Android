package com.adrosonic.craftexchange.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.RelatedProducts
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.*
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList

class ProductUpdateService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(ProductUpdateService.KEY_ID)
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        updateProductAction(longId)
    }

    private fun updateProductAction(prodId: Long){
        try {
            if (prodId > 0){
                Log.e("Offline","prodId :"+prodId)
                val productEntry= ProductPredicates.getArtisanProducts(prodId)
                if(productEntry!=null) {
                    Log.e("Offline", "Product Id :" + productEntry.productId)
                    val imageEntry =ProductImagePredicates.getImagesList(productEntry.productId ?: 0)
                    val weaveIds = WeaveTypesPredicates.getWeaveList(productEntry.productId ?: 0)
                    val careIds = ProductCaresPredicates.getCareList(productEntry.productId ?: 0)
                    val relatedEntry = RelateProductPredicates.getRelatedProductOfProduct(
                        productEntry.productId ?: 0)
                    Log.e("Offline", "imageList :" + imageEntry.joinToString())
                    var productData =createProductTemplateString(productEntry, weaveIds, careIds, relatedEntry)
                    Log.e("Offline", "productData :" + productData)
                    updateProduct(productData, imageEntry, productEntry.productId ?:0)
                }
                }
        }catch (e: Exception){
            Log.e("Exception Deleting",e.message)
        }
    }

    fun createProductTemplateString(productEntry: ArtisanProducts?, weaveIds:List<Long>, careIdList:List<Long>, realatedEntry: RelatedProducts?):String{

        Log.e("Offline","createProductTemplateString 1111111111:"+ weaveIds.size)

        var relList=ArrayList<RelProduct>()
        var careList=ArrayList<ProductCare>()
        var weafList=ArrayList<ProductWeaf>()
        if(realatedEntry!=null)  {
            var relatedProductObj= RelProduct(realatedEntry.productTypeId?:0,realatedEntry.productWidth?:"",realatedEntry.productLength?:"")
            relList.add(relatedProductObj)
        }
        weaveIds.forEach {
            var productWeaf=ProductWeaf(it,productEntry?.productId?:0,it)
            weafList.add(productWeaf)
        }
        careIdList.forEach {
            var productCare=ProductCare(it,it,productEntry?.productId?:0)
            careList.add(productCare)
        }

        var template= UpdateProductTemplateRequest(productEntry?.productCode?:"",productEntry?.extraWeftDyeId?:0,productEntry?.extraWeftYarnCount?:"",
            productEntry?.extraWeftYarnId?:0,productEntry?.gsm?:"",productEntry?.productId?:0,productEntry?.productLength?:"",careList,
            productEntry?.productCategoryId?:0,productEntry?.productStatusId?:0,productEntry?.productTypeId?:0,weafList,
            productEntry?.productSpecs?:"",productEntry?.reedCountId?:0,relList,productEntry?.productTag?:"",productEntry?.warpDyeId?:0,
            productEntry?.warpYarnCount?:"",productEntry?.warpYarnId?:0,productEntry?.weftDyeId?:0,productEntry?.weftYarnCount?:"",
            productEntry?.weftYarnId?:0,productEntry?.weight?:"",productEntry?.productWidth?:"")

        Log.e("Offline","template :"+ Gson().toJson(template))
        return Gson().toJson(template)
    }


    fun updateProduct(productData:String,imageList:ArrayList<String>?,prodId:Long){
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

        CraftExchangeRepository.getProductService()
            .updateProductTemplate(token,headerBoundary,dataLength, productData, bodyMultipart)
            .enqueue(object : Callback, retrofit2.Callback<ArtisanProductTemplateRespons> {
                override fun onFailure(
                    call: Call<ArtisanProductTemplateRespons>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    Log.e("Offline", "getProductUploadData onFailure: " + t.localizedMessage)
                }

                override fun onResponse(
                    call: Call<ArtisanProductTemplateRespons>,
                    response: retrofit2.Response<ArtisanProductTemplateRespons>
                ) {
                    Log.e("Offline", "onResponse :" + response.code())
                    if (response.body()?.valid == true) {
                        ProductPredicates.updateProductEntryPostUpdate(prodId)
                        ProductImagePredicates.deleteProdImageForUpdate(prodId)
                    }
                }
            })

    }

    private fun prepareMultiPartBody(
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


    companion object {
        const val KEY_ID = "prod_id"
        private const val JOB_ID = 3000
        private const val TAG="ProductUpdateService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, ProductUpdateService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}