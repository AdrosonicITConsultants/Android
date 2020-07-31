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
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.File
import javax.security.auth.callback.Callback

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
                Log.e("Offline","weaveIds :"+weaveIds?.joinToString ())
                Log.e("Offline","careIds :"+careIds?.joinToString ())
                Log.e("Offline","relatedEntry :"+relatedEntry?.productWeight)
               var productData=createProductTemplateString(productEntry,weaveIds,careIds,relatedEntry)
                Log.e("Offline","productData :"+productData)
                uploadProduct(productData,imageEntry,prodId)
          }
        }catch (e: Exception){
            Log.e("Offline",e.message)
        }
    }
    fun createProductTemplateString(productEntry:ArtisanProducts?,weaveIds:List<Long>,careIdList:List<Long>,realatedEntry:RelatedProducts?):String{
        var template= ArtisanAddProductRequest()
        Log.e("Offline","createProductTemplateString 1111111111:"+weaveIds?.size)
        template.tag=productEntry?.productTag?:""//et_prod_name.text.toString()
        template.code=productEntry?.productCode?:""
        template.productCategoryId=productEntry?.productCategoryId?:0
        Log.e("Offline","createProductTemplateString 22222222222:"+weaveIds?.size)
        template.productTypeId=productEntry?.productTypeId?:0
        template.productSpec=productEntry?.productSpecs?:""
        template.weight=productEntry?.weight?:""
        template.careIds=careIdList
        template.weaveIds=weaveIds
        Log.e("Offline","createProductTemplateString 333333333:"+careIdList?.size)
        template.statusId=productEntry?.productStatusId?:1
        template.gsm=productEntry?.gsm?:""
        template.warpDyeId=productEntry?.warpDyeId?:0
        template.warpYarnCount=productEntry?.warpYarnCount?:""
        Log.e("Offline","createProductTemplateString 4444444444:"+template.statusId)
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
        Log.e("Offline","createProductTemplateString 6666666666:"+template?.reedCountId)
        if(realatedEntry!=null)  {
            var relatedProductObj= RelatedProduct()
            relatedProductObj.length=realatedEntry.productLength?:""
            relatedProductObj.width=realatedEntry.productWidth?:""
            relatedProductObj.productTypeID=realatedEntry.productTypeId?:0
            template.relatedProduct=relatedProductObj.toString()
        }
        Log.e("Offline","template 777777777 :"+template.code)
        return template.toString()
    }

    fun uploadProduct(productData:String,imageList:ArrayList<String>?,prodId:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        Log.e("Offline","productData :"+productData)
        if(imageList!!.size==3) {
            var file1 = File(imageList?.get(0))
            var file2 = File(imageList?.get(1))
            var file3 = File(imageList?.get(2))
            val body1 = prepareFilePart("file1", file1)
            val body2 = prepareFilePart("file2", file2)
            val body3 = prepareFilePart("file3", file3)

            CraftExchangeRepository.getProductService()
                .uploadProductTemplate(token, productData, body1, body2, body3)
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
                            deleteOfflineEntries(prodId)
                        }
                    }
                })
        }else if(imageList!!.size==2) {
            var file1 = File(imageList?.get(0))
            var file2 = File(imageList?.get(1))
            val body1 = prepareFilePart("file1", file1)
            val body2 = prepareFilePart("file2", file2)

            CraftExchangeRepository.getProductService()
                .uploadProductTemplate(token, productData, body1, body2)
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
                            deleteOfflineEntries(prodId)
                        }
                    }
                })
        }else {
            var file1 = File(imageList?.get(0))
            val body1 = prepareFilePart("file1", file1)

            CraftExchangeRepository.getProductService()
                .uploadProductTemplate(token, productData, body1)
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
                            deleteOfflineEntries(prodId)
                        }
                    }
                })
        }
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