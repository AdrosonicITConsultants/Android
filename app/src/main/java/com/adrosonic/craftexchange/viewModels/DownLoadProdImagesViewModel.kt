package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchange.database.predicates.ProductImagePredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.ImageDownloadRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.repository.data.response.buyer.ownDesign.AddOwnDesignResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList


class DownLoadProdImagesViewModel (application: Application) : AndroidViewModel(application) {
    interface DownloadImagesCallback {
        fun onSuccess(imageList:ArrayList<String>)
        fun onFailure()
    }
    interface UpdateProductCallback {
        fun onUpdateSuccess()
        fun onUpdateFailure()
    }
    var downloadcnt=0
    var listener: DownloadImagesCallback? = null
    var updateListener: UpdateProductCallback? = null
    companion object{
        const val TAG="DownLoadProdImages"
    }
    fun downLoadImages(productId:Long,imageList:ArrayList<String>){
        downloadcnt=imageList.size
        for(imageName in imageList) {
            Log.e(TAG, "111111 : $imageName ProductId: $productId" )
            ImageDownloadRepository
                .getBuyerOwnDesignService()
                .getProductImage(productId, imageName)
                .enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                        Log.e(TAG, "onFailure: " + t.localizedMessage)
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: retrofit2.Response<ResponseBody>
                    ) {
                        Log.e(TAG, "222222222222 : " + imageName)
                        val body = response?.body()
                        if (body != null) {
                            downloadcnt--
                            writeResponseBodyToDisk( imageList,body,imageName, getApplication(), downloadcnt )
                        } else {
                            listener?.onFailure()
                        }
                    }

                })
        }
    }
    fun downLoadArtisanImages(productId:Long,imageList:ArrayList<String>){
        downloadcnt=imageList.size
        for(imageName in imageList) {
            Log.e(TAG, "111111 : $imageName ProductId: $productId" )
            ImageDownloadRepository
                .getProductService()
                .getProductImage(productId, imageName)
                .enqueue(object : Callback, retrofit2.Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                        Log.e(TAG, "onFailure: " + t.localizedMessage)
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: retrofit2.Response<ResponseBody>
                    ) {
                        Log.e(TAG, "222222222222 : " + imageName)
                        val body = response?.body()
                        if (body != null) {
                            downloadcnt--
                            writeResponseBodyToDisk( imageList,body,imageName, getApplication(), downloadcnt )
                        } else {
                            listener?.onFailure()
                        }
                    }

                })
        }
    }
    fun writeResponseBodyToDisk(
        imageList:ArrayList<String>,
        body: ResponseBody,
        imageName: String,
        context: Context,
        cntr:Int
    ): Boolean {
        try {
            if (!File(context.cacheDir,  Utility.BROWSING_IMGS).exists()) File(context.cacheDir, Utility.BROWSING_IMGS).mkdir()
//                if (!File(context.cacheDir, Utility.BROWSING_IMGS).exists()) File(context.cacheDir,"attachment/"+artifactId).mkdir()

            val myDir = File(context.cacheDir, "/"+ Utility.BROWSING_IMGS+"/$imageName")
            if(imageName!!.length>42){
                val renamed = File(context.cacheDir, Utility.BROWSING_IMGS + "/" + System.currentTimeMillis()+ ".jpg")
                myDir.renameTo( renamed)
            }
            var inputStream: InputStream = body.byteStream()
            var outputStream: OutputStream = FileOutputStream(myDir)
            try {
                var fileReader = ByteArray(4096)

                var fileSize = body.contentLength()
                var fileSizeDownloaded = 0
                while (true) {
                    var read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read
                }
                outputStream.flush()
                Log.e(TAG, "4444444444444 : " + cntr)
                if(cntr.equals(0))  listener?.onSuccess(imageList)

                return true
            } catch (e: IOException) {
                return false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            return false
        }
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
        val body = byteData!!.array().toRequestBody(MediaType.parse("image/*"), 0, byteData.capacity())
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
                    updateListener?.onUpdateFailure()
                }
                override fun onResponse(
                    call: Call<AddOwnDesignResponse>,
                    response: retrofit2.Response<AddOwnDesignResponse>
                ) {
                    Log.e("Offline", "onResponse :" + response.code())
                    Log.e("Offline", "onResponse :" + response.body()?.valid)
                    if (response.body()?.valid == true) {
                        deleteOfflineEntries(prodId)
                        updateListener?.onUpdateSuccess()
                    }else{
                        updateListener?.onUpdateFailure()
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
}