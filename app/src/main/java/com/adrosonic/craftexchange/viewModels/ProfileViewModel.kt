package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.entities.realmEntities.UserAddress
import com.adrosonic.craftexchange.database.predicates.AddressPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    interface FetchUserDetailsInterface{
        fun onSuccess()
        fun onFailure()
    }

    var listener: FetchUserDetailsInterface? = null
    val userDetails : MutableLiveData<CraftUser> by lazy { MutableLiveData<CraftUser>() }
    val redAddrDetails : MutableLiveData<UserAddress> by lazy { MutableLiveData<UserAddress>() }

    fun getUserMutableData(): MutableLiveData<CraftUser> {
        userDetails.value=loadUserData()
        return userDetails
    }

    fun loadUserData(): CraftUser? {
        var userDetails= UserPredicates.findUser(Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        return userDetails
    }

    fun getUserAddrMutableData(): MutableLiveData<UserAddress> {
        redAddrDetails.value=loadAddrData()
        return redAddrDetails
    }

    fun loadAddrData(): UserAddress? {
        var addrDetails= AddressPredicates.getAddressAddrType(ConstantsDirectory.REGISTERED,
            Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        return addrDetails
    }


    fun getProfileDetails(context : Context){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

        CraftExchangeRepository
            .getUserService()
            .viewMyProfile(token).enqueue(object : Callback, retrofit2.Callback<ProfileResponse> {
                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    t.printStackTrace()
                    listener?.onFailure()
                }

                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    if(response.body()?.valid == true){
                        UserPredicates.refreshArtisanDetails(response.body())
                        ProductPredicates.insertArtisanProductCategory(response.body())
                        UserPredicates.insertPaymentDetails(response.body())
                        AddressPredicates.refreshUserAddress(response.body())
                        listener?.onSuccess()
                    }else{
                        listener?.onFailure()
                    }
                }
            })
    }
}