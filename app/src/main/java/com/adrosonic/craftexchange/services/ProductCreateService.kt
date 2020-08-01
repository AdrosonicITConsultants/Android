package com.adrosonic.craftexchange.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
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
                //todo base upton itemId/product id fire select query for mark deletion to have neccesary parameters to hit delte API
               val productEntry=ProductPredicates.getArtisanProducts(prodId)
                uploadProduct("",null)
          }
        }catch (e: Exception){
            Log.e("Exception Deleting",e.message)
        }
    }

    fun uploadProduct(productData:String,imageList:ArrayList<String>?){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        val productData1="{\"tag\":\"ekansh\",\"code\":\"ekansh\",\"productCategoryId\":3,\"productTypeId\":3,\"productSpec\":\"asdasd\",\"weight\":\"asdasd\",\"careIds\":[7],\"weaveIds\":[4,6],\"statusId\":2,\"gsm\":\"\",\"warpDyeId\":1,\"warpYarnCount\":\"50nm\",\"warpYarnId\":2,\"weftDyeId\":2,\"weftYarnCount\":\"2/20s\",\"weftYarnId\":1,\"extraWeftDyeId\":1,\"extraWeftYarnCount\":\"2/10s\",\"extraWeftYarnId\":1,\"width\":\"46 inches\",\"length\":\"5.5 mtr\",\"reedCountId\":\"6\"}"
        Log.e(TAG,"productData :"+productData)
        var file1 = File(imageList?.get(0))
        var file2 = File(imageList?.get(1))
        var file3 = File(imageList?.get(2))

        val body1 = prepareFilePart("file1", file1)
        val body2 = prepareFilePart("file2", file2)
        val body3 = prepareFilePart("file3", file3)

        CraftExchangeRepository
            .getProductService()
            .uploadProductTemplate(token,productData1,body1,body2,body3)
            .enqueue(object: Callback, retrofit2.Callback<ArtisanProductTemplateRespons> {
                override fun onFailure(call: Call<ArtisanProductTemplateRespons>, t: Throwable) {
                    t.printStackTrace()
                    Log.e(TAG,"getProductUploadData onFailure: "+t.stackTrace.joinToString())
                    Log.e(TAG,"getProductUploadData onFailure: "+t.localizedMessage)
                }
                override fun onResponse(
                    call: Call<ArtisanProductTemplateRespons>,
                    response: retrofit2.Response<ArtisanProductTemplateRespons>) {
                    Log.e(TAG,"onResponse :"+response.code())
                    Log.e(TAG,"onResponse :"+response.isSuccessful)
                    Log.e(TAG,"onResponse :"+call.request().url)
                    if(response.body()?.valid == true){
                        Log.e(TAG,"getProductUploadData :"+response.body()?.data?.artitionID)
//                        todo update DB table
//                        UserConfig.shared.productUploadJson= Gson().toJson(response.body())
//                        Log.e("LandingViewModel","SPF :"+ UserConfig.shared.productUploadJson)


                    }else{
                        Log.e(TAG,"getProductUploadData body null: "+response.body()?.errorCode)
                    }
                }

            })
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