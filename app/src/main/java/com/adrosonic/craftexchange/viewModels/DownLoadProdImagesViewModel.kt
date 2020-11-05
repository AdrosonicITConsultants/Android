package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.ImageDownloadRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.*
import javax.security.auth.callback.Callback


class DownLoadProdImagesViewModel (application: Application) : AndroidViewModel(application) {
    interface DownloadImagesCallback {
        fun onSuccess(imageList:ArrayList<String>)
        fun onFailure()
    }
var downloadcnt=0
var listener: DownloadImagesCallback? = null
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

}