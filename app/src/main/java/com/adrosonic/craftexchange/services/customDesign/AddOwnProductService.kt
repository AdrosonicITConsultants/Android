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
import com.adrosonic.craftexchange.repository.data.request.buyer.OwnDesignRequest
import com.adrosonic.craftexchange.repository.data.request.buyer.RelatedProduct
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.AddOwnDesignResponse
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


class AddOwnProductService : JobIntentService() {

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
                val productEntry = BuyerCustomProductPredicates.getCustomProduct(prodId)
                val imageEntry = ProductImagePredicates.getImagesList(prodId)
                val relatedEntry = RelateProductPredicates.getRelatedProductOfProduct(prodId)
                Log.e("Offline", "imageList :" + imageEntry.joinToString())
                var productData =createProductTemplateString(productEntry,relatedEntry)
                Log.e("Offline", "productData :" + productData)
                uploadProduct(productData, imageEntry, prodId)
            }
        } catch (e: Exception) {
            Log.e("Offline", "Exception " + e.localizedMessage)
        }
    }

    fun createProductTemplateString(productEntry: BuyerCustomProduct? ,realatedEntry: RelatedProducts?): String {
        var template = OwnDesignRequest()
        Log.e("Offline", "createProductTemplateString 1111111111:" + productEntry?.weaveIds)
        template.productCategoryId = productEntry?.productCategoryId ?: 0
        template.productTypeId = productEntry?.productTypeId ?: 0
        template.productSpec = productEntry?.productSpe ?: ""
        template.weight = productEntry?.weight ?: ""
        template.weaveIds = "${productEntry?.weaveIds}"
        template.gsm = productEntry?.gsm ?: ""
        template.warpDyeId = productEntry?.warpDyeId ?: 0
        template.warpYarnCount = productEntry?.warpYarnCount ?: ""
        template.warpYarnId = productEntry?.warpYarnId ?: 0
        template.weftDyeId = productEntry?.weftDyeId ?: 0
        template.weftYarnCount = productEntry?.weftYarnCount ?: ""
        template.weftYarnId = productEntry?.weftYarnId ?: 0
        template.extraWeftYarnId = productEntry?.extraWeftYarnId ?: 0
        template.extraWeftYarnCount = productEntry?.extraWeftYarnCount ?: ""
        template.extraWeftDyeId = productEntry?.extraWeftDyeId ?: 0
        template.width = productEntry?.width ?: ""
        template.length = productEntry?.length ?: ""
        template.reedCountId = productEntry?.reedCountId.toString()
        if (realatedEntry != null) {
            var relatedProductObj = RelatedProduct()
            relatedProductObj.length = realatedEntry.productLength ?: ""
            relatedProductObj.width = realatedEntry.productWidth ?: ""
            relatedProductObj.productTypeID = realatedEntry.productTypeId ?: 0
            template.relatedProduct = relatedProductObj.toString()
        }
        Log.e("Offline", "createProductTemplateString endd:" + productEntry?.weaveIds)
        return template.toString()
    }

    fun uploadProduct(productData: String, imageList: ArrayList<String>?, prodId: Long) {
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN, "")}"
        Log.e("Offline", "productData :" + productData)

        var dataLength = 0L
        imageList?.forEach {
            dataLength = dataLength + File(it).length()
        }
        var boundary = UUID.randomUUID().toString()
        var headerBoundary = "multipart/form-data;boundary=" + boundary

        val byteData = prepareMultiPartBody(boundary, dataLength, imageList)
        Log.e("Offline", "prepareMultiPartBody 666666 " + byteData?.capacity())
        val body = byteData!!.array().toRequestBody(MediaType.parse("image/*"), 0, byteData.capacity())
        Log.e("Offline", "prepareMultiPartBody 77777 " + body.contentLength())
        val bodyMultipart = MultipartBody.Builder()
            .addPart(body)
            .build()
        Log.e("Offline", "prepareMultiPartBody 88888 " + bodyMultipart.boundary)
        CraftExchangeRepository.getBuyerOwnDesignService()
            .uploadOwnProduct(token, headerBoundary, dataLength, productData, bodyMultipart)
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
        var boundaryPrefix = "--$boundary\n"
        imageList?.forEach {
            ctr++
            body.put(boundaryPrefix.toByteArray(StandardCharsets.UTF_8))
            val file = File(it)
//            Content-Disposition: form-data; name="file2"; filename="Screenshot_20200801-115847.png"
//            Content-Type: image/png
            var contentDisposition =
                "Content-Disposition: form-data; name=file$ctr; filename=${file.name}\r\n"
            body.put(contentDisposition.toByteArray(StandardCharsets.UTF_8))
            var mimetype = MediaType.parse("image/*")
            var mimeType = "Content-Type: $mimetype\r\n\r\n"
            body.put(mimeType.toByteArray(StandardCharsets.UTF_8))
            body.put(file.readBytes())
            body.put("\r\n".toByteArray(StandardCharsets.UTF_8))

            if (ctr == imageList.size) {
                var bottomBoundaryStr = "--$boundary--"
                body.put(bottomBoundaryStr.toByteArray(StandardCharsets.UTF_8))
            }
            Log.e("Offline", "prepareMultiPartBody $body")
        }
        return body
    }


    private fun deleteOfflineEntries(prodId: Long) {
        BuyerCustomProductPredicates.deleteProductEntry(prodId)
//        RelateProductPredicates.deleteRelatedProduct(prodId)
        ProductImagePredicates.deleteProdImages(prodId)
    }


    companion object {
        const val KEY_ID = "own_prod_id"
        private const val JOB_ID = 5000
        private const val TAG = "AddOwnProductService"
        fun enqueueWork(context: Context, work: Intent) {
            try {
                enqueueWork(context, AddOwnProductService::class.java, this.JOB_ID, work)
            } catch (e: Exception) {
                Log.e("EnqueueWork Delete", e.message)
            }
        }
    }
}
