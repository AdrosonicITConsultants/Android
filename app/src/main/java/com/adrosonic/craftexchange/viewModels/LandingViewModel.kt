package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.products.ArtisanProductDetailsResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import javax.security.auth.callback.Callback

class LandingViewModel(application: Application) : AndroidViewModel(application) {

    fun logoutUser() {
        val editor = Prefs.edit()
        editor.clear()
        editor.commit()
        editor.apply()
    }

    fun getProductsOfArtisan(context : Context){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getArtisanProducts(token)
            .enqueue(object: Callback, retrofit2.Callback<ArtisanProductDetailsResponse> {
            override fun onFailure(call: Call<ArtisanProductDetailsResponse>, t: Throwable) {
                t.printStackTrace()
            }
            override fun onResponse(
                call: Call<ArtisanProductDetailsResponse>,
                response: retrofit2.Response<ArtisanProductDetailsResponse>) {
                if(response.body()?.valid == true){
                    ProductPredicates.insertProductsOfArtisan(response.body())
                }else{
                   Utility.displayMessage(response.body()?.errorMessage.toString(),context)
                }
            }

        })
    }
}