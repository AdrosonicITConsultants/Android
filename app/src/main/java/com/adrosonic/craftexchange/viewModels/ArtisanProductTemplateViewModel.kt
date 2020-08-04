package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.ArtisanProductTemplateRespons
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.File
import javax.security.auth.callback.Callback


class ArtisanProductTemplateViewModel (application: Application) : AndroidViewModel(application) {

companion object{
    const val TAG="ArtisanProduct"
}
    fun uploadProduct(productData:String,imageList:ArrayList<String>){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        Log.e(TAG,"productData :"+productData)
        var file1 = File(imageList.get(0))
        var file2 = File(imageList.get(1))
        var file3 = File(imageList.get(2))

        val body1 = prepareFilePart("file1", file1)
        val body2 = prepareFilePart("file2", file2)
        val body3 = prepareFilePart("file3", file3)

//        CraftExchangeRepository
//            .getProductService()
//            .uploadProductTemplate(token,productData,body1,body2,body3)
//            .enqueue(object: Callback, retrofit2.Callback<ArtisanProductTemplateRespons> {
//                override fun onFailure(call: Call<ArtisanProductTemplateRespons>, t: Throwable) {
//                    t.printStackTrace()
//                    Log.e(TAG,"getProductUploadData onFailure: "+t.stackTrace.joinToString())
//                    Log.e(TAG,"getProductUploadData onFailure: "+t.localizedMessage)
//                }
//                override fun onResponse(
//                    call: Call<ArtisanProductTemplateRespons>,
//                    response: retrofit2.Response<ArtisanProductTemplateRespons>) {
//                    Log.e(TAG,"onResponse :"+response.code())
//                    Log.e(TAG,"onResponse :"+response.isSuccessful)
//                    Log.e(TAG,"onResponse :"+call.request().url)
//                    if(response.body()?.valid == true){
//                         Log.e(TAG,"getProductUploadData :"+response.body()?.data?.artitionID)
////                        todo update DB table
////                        UserConfig.shared.productUploadJson= Gson().toJson(response.body())
////                        Log.e("LandingViewModel","SPF :"+ UserConfig.shared.productUploadJson)
//
//
//                    }else{
//                        Log.e(TAG,"getProductUploadData body null: "+response.body()?.errorCode)
//                    }
//                }
//
//            })
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

}