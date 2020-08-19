package com.adrosonic.craftexchange.services.customDesign

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchange.database.entities.realmEntities.RelatedProducts
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.buyer.RelProduct
import com.adrosonic.craftexchange.repository.data.request.buyer.ProductWeaf
import com.adrosonic.craftexchange.repository.data.request.buyer.UpdateOwnDesignRequest
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.AddOwnDesignResponse
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

class UpdateOwnProductService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        var itemId = intent.getStringExtra(KEY_ID)
        if (itemId.isEmpty()) itemId = "-1"
        var longId = itemId.toLong()
        Log.e("Offline", "prodId 0000000000:" + longId)
        addProductAction(longId)
    }

    private fun addProductAction(prodId: Long) {
        try {
            Log.e("Offline", "prodId 11111111111:" + prodId)
            if (prodId > 0) {
                Log.e("Offline", "prodId :" + prodId)
                val productEntry = BuyerCustomProductPredicates.getCustomProductFormRemotId(prodId)
                val imageEntry = ProductImagePredicates.getImagesList(prodId)
                val relatedEntry = RelateProductPredicates.getRelatedProductOfProduct(prodId)
                val weaveTypes = WeaveTypesPredicates.getWeaveList(prodId)
                Log.e("Offline", "imageList :" + imageEntry?.joinToString())
                var productData =createOwnProductString(productEntry,relatedEntry,weaveTypes)
                uploadProduct(productData, imageEntry, prodId)
            }
        } catch (e: Exception) {
            Log.e("Offline", "Exception " + e.localizedMessage)
        }
    }

    fun createOwnProductString(productEntry: BuyerCustomProduct? ,realatedEntry: RelatedProducts?,weaveList:List<Long>?): String {
        var relList=ArrayList<RelProduct>()
        var weafList=ArrayList<ProductWeaf>()
        if(realatedEntry!=null)  {
            var relatedProductObj=RelProduct(
                    realatedEntry.productTypeId ?: 0,
                    realatedEntry.productWidth ?: "",
                    realatedEntry.productLength ?: ""
                )
            relList.add(relatedProductObj)
        }
        weaveList?.forEach {
            var productWeaf= ProductWeaf(it,productEntry?.id?:0,it)
            weafList.add(productWeaf)
        }
        Log.e("Offline", "createOwnProductString ${productEntry?.extraWeftDyeId}")
        var template = UpdateOwnDesignRequest(productEntry?.extraWeftDyeId?:0,productEntry?.extraWeftYarnCount?:"",productEntry?.extraWeftYarnId?:0,
            productEntry?.gsm?:"",productEntry?.id?:0,productEntry?.length?:"",productEntry?.productCategoryId?:0,productEntry?.productTypeId?:0,
            weafList ,productEntry?.productSpe?:"",productEntry?.reedCountId?:0,relList,productEntry?.warpDyeId?:0,productEntry?.warpYarnCount?:"",
            productEntry?.warpYarnId?:0,productEntry?.weftDyeId?:0,productEntry?.weftYarnCount?:"",productEntry?.weftYarnId?:0,productEntry?.weight?:"",productEntry?.width?:"")

        Log.e("Offline","template :"+ Gson().toJson(template))
        return Gson().toJson(template)
    }

    fun uploadProduct(productData: String, imageList: ArrayList<String>?, prodId: Long) {
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN, "")}"
        Log.e("Offline", "productData :" + productData)

        var dataLength = 0L
        imageList?.forEach {
            dataLength = dataLength + File(it).length()
        }
        var boundary = UUID.randomUUID().toString()
        var headerBoundary = "multipart/form-data;boundary=$boundary"

        val byteData = prepareMultiPartBody(boundary, dataLength, imageList)
        Log.e("Offline", "prepareMultiPartBody 666666 " + byteData?.capacity())
        val body = byteData!!.array().toRequestBody(MediaType.parse("image/*"), 0, byteData!!.capacity())
        Log.e("Offline", "prepareMultiPartBody 77777 " + body.contentLength())
        val bodyMultipart = MultipartBody.Builder()
            .addPart(body)
            .build()
        Log.e("Offline", "prepareMultiPartBody 88888 " + bodyMultipart.boundary)
        CraftExchangeRepository.getBuyerOwnDesignService()
            .updateOwnProduct(token, headerBoundary, dataLength, productData, bodyMultipart)
            .enqueue(object : Callback, retrofit2.Callback<AddOwnDesignResponse> {
                override fun onFailure(
                    call: Call<AddOwnDesignResponse>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    Log.e("Offline", "getProductUploadData onFailure: " + t.localizedMessage)
                }
                override fun onResponse(
                    call: Call<AddOwnDesignResponse>,
                    response: retrofit2.Response<AddOwnDesignResponse>
                ) {
                    Log.e("Offline", "onResponse :" + response.code())
                    Log.e("Offline", "onResponse :" + response.body()?.valid)
                    if (response.body()?.valid == true) {
                        deleteOfflineEntries(prodId)
                    }
                }
            })
    }

    private fun prepareMultiPartBody(
        boundary: String,
        dataLength: Long,
        imageList: ArrayList<String>?
    ): ByteBuffer? {
        var ctr = 0
        var body = ByteBuffer.allocate(dataLength.toInt() + 5000)
        Log.e("Offline","prepareMultiPartBody 1111111")
        var boundaryPrefix = "--$boundary\n"
        Log.e("Offline","prepareMultiPartBody 2222222")
        imageList?.forEach {
            ctr++
            body.put(boundaryPrefix.toByteArray(StandardCharsets.UTF_8))
            val file = File(it)
//            Content-Disposition: form-data; name="file2"; filename="Screenshot_20200801-115847.png"
//            Content-Type: image/png
            var contentDisposition ="Content-Disposition: form-data; name=file$ctr; filename=${file.name}\r\n"
            body.put(contentDisposition.toByteArray(StandardCharsets.UTF_8))
            var mimetype = MediaType.parse("image/*")
            var mimeType = "Content-Type: $mimetype\r\n\r\n"
            body.put(mimeType.toByteArray(StandardCharsets.UTF_8))
            body.put(file.readBytes())
            body.put("\r\n".toByteArray(StandardCharsets.UTF_8))
            Log.e("Offline","prepareMultiPartBody 333333333")
            if (ctr == imageList.size) {
                var bottomBoundaryStr = "--$boundary--"
                body.put(bottomBoundaryStr.toByteArray(StandardCharsets.UTF_8))
            }
            Log.e("Offline", "prepareMultiPartBody 555555555555")
        }
        return body
    }


    private fun deleteOfflineEntries(prodId: Long) {
        BuyerCustomProductPredicates.updateProductEntryPostUpdate(prodId)
//        RelateProductPredicates.deleteRelatedProduct(prodId)
        ProductImagePredicates.deleteProdImageForUpdate(prodId)
    }


    companion object {
        const val KEY_ID = "own_prod_id"
        private const val JOB_ID = 5000
        private const val TAG = "AddOwnProductService"
        fun enqueueWork(context: Context, work: Intent) {
            try {
                enqueueWork(context, UpdateOwnProductService::class.java, this.JOB_ID, work)
            } catch (e: Exception) {
                Log.e("EnqueueWork Delete", e.message)
            }
        }
    }
}
