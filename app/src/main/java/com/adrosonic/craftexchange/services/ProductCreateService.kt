package com.adrosonic.craftexchange.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.RelatedProducts
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.ArtisanAddProductRequest
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.RelatedProduct
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.utils.ConstantsDirectory
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

class ProductCreateService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(ProductCreateService.KEY_ID)
        if(itemId.isEmpty())itemId="-1"
        var longId=itemId.toLong()
        addProductAction(longId)
    }

    private fun addProductAction(prodId: Long){
        try {
            if (prodId > 0){
               Log.e("Offline","prodId :"+prodId)
               val productEntry=ProductPredicates.getArtisanProducts(prodId)
               val imageEntry=ProductImagePredicates.getImagesList(prodId)
               val weaveIds=WeaveTypesPredicates.getWeaveList(prodId)
               val careIds=ProductCaresPredicates.getCareList(prodId)
               val relatedEntry=RelateProductPredicates.getRelatedProductOfProduct(prodId)
               Log.e("Offline","imageList :"+imageEntry?.joinToString ())
               var productData=createProductTemplateString(productEntry,weaveIds,careIds,relatedEntry)
               Log.e("Offline","productData :"+productData)
               uploadProduct(productData,imageEntry,prodId)
          }
        }catch (e: Exception){
            Log.e("Offline","Exception "+e.localizedMessage)
        }
    }
    fun createProductTemplateString(productEntry:ArtisanProducts?,weaveIds:List<Long>,careIdList:List<Long>,realatedEntry:RelatedProducts?):String{
        var template= ArtisanAddProductRequest()
        Log.e("Offline","createProductTemplateString 1111111111:"+weaveIds?.size)
        template.tag=productEntry?.productTag?:""//et_prod_name.text.toString()
        template.code=productEntry?.productCode?:""
        template.productCategoryId=productEntry?.productCategoryId?:0
        template.productTypeId=productEntry?.productTypeId?:0
        template.productSpec=productEntry?.productSpecs?:""
        template.weight=productEntry?.weight?:""
        template.careIds=careIdList
        template.weaveIds=weaveIds
        template.statusId=productEntry?.productStatusId?:1
        template.gsm=productEntry?.gsm?:""
        template.warpDyeId=productEntry?.warpDyeId?:0
        template.warpYarnCount=productEntry?.warpYarnCount?:""
        template.warpYarnId=productEntry?.warpYarnId?:0
        template.weftDyeId=productEntry?.weftDyeId?:0
        template.weftYarnCount=productEntry?.weftYarnCount?:""
        template.weftYarnId=productEntry?.weftYarnId?:0
        template.extraWeftYarnId=productEntry?.extraWeftYarnId?:0
        template.extraWeftYarnCount=productEntry?.extraWeftYarnCount?:""
        template.extraWeftDyeId=productEntry?.extraWeftDyeId?:0
        template.width=productEntry?.productWidth?:""
        template.length=productEntry?.productLength?:""
        template.reedCountId=productEntry?.reedCountId.toString()
        if(realatedEntry!=null)  {
            var relatedProductObj= RelatedProduct()
            relatedProductObj.length=realatedEntry.productLength?:""
            relatedProductObj.width=realatedEntry.productWidth?:""
            relatedProductObj.productTypeID=realatedEntry.productTypeId?:0
            template.relatedProduct=relatedProductObj.toString()
        }
        Log.e("Offline","template :"+template.code)
        return template.toString()
    }

    fun uploadProduct(productData:String,imageList:ArrayList<String>?,prodId:Long){
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
            val body = byteData!!.array().toRequestBody(MediaType.parse("image/*"), 0, byteData!!.capacity())
            Log.e("Offline","prepareMultiPartBody 77777 "+body.contentLength())
            val bodyMultipart = MultipartBody.Builder()
                .addPart(body)
                .build()
            Log.e("Offline","prepareMultiPartBody 88888 "+bodyMultipart.boundary)
            CraftExchangeRepository.getProductService()
                .uploadProductTemplate(token,headerBoundary,dataLength, productData, bodyMultipart)
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
                        Log.e("Offline", "onResponse : ${response.isSuccessful} and ${response.message()}")
                        if (response.body()?.valid == true) {
                            deleteOfflineEntries(prodId)
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
            var mimetype=MediaType.parse("image/*")
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
    private fun prepareFilePart(
        partName: String,
        fileUri: File
    ): MultipartBody.Part? {
        val file: File = fileUri
        val requestFile= file.toRequestBody(MediaType.parse("image/*"))
        Log.e(TAG,"MultipartBody :"+partName+" Name:  "+file.name)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
    private fun deleteOfflineEntries(prodId:Long){
        ProductPredicates.deleteArtisanProductTemplatePOstUpload(prodId)
        RelateProductPredicates.deleteRelatedProduct(prodId)
        ProductImagePredicates.deleteProdImages(prodId)
        WeaveTypesPredicates.deleteWeaveIds(prodId)
        ProductCaresPredicates.deleteCareIds(prodId)
    }


    companion object {
        const val KEY_ID = "prod_id"
        private const val JOB_ID = 1000
        private const val TAG="ProductCreateService"
        fun enqueueWork(context: Context, work: Intent){
            try {
                enqueueWork(context, ProductCreateService::class.java, this.JOB_ID,work)
            }catch (e: Exception){
                Log.e("EnqueueWork Delete",e.message)
            }
        }
    }
}