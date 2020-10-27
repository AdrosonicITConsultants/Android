package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserStatusResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.collection_ktx.onlyNumbersList
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class UserProfileViewModal(application: Application) : AndroidViewModel(application)  {

    interface ProfileDataInterface{
        fun onProfileFailure()
        fun onProfileSuccess()
    }
    interface ActivateInterface{
        fun onActivateFailure()
        fun onActivateSuccess()
    }
    interface DeactivateInterface{
        fun onDeactivateFailure()
        fun onDeactivateSuccess()
    }
    interface setRatinginterface{
        fun onRatingSuccess()
        fun onRatingFailure()
    }

    var profileListener : ProfileDataInterface?= null
    var activateListener : ActivateInterface?= null
    var deactivateListener : DeactivateInterface?= null
    var setratingListener : setRatinginterface? =null


    fun getUserData(id:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
//        Utility.displayMessage(token,Context)

        craftexchangemarketingRepository
            .getIndividualUserService()
            .getUserData(
                token ,
                id
            ).enqueue(object : Callback, retrofit2.Callback<UserProfileResponse> {
                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    t.printStackTrace()
                    profileListener?.onProfileFailure()
                }
                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    if (response?.isSuccessful) {
                        profileListener?.onProfileSuccess()
                        if(response.body()?.valid == true){
                            UserConfig.shared.indUserDataJson= Gson().toJson(response.body())
                        }


//                        var itr = response?.body()?.iterator()
//                        if(itr != null){
//                            while (itr.hasNext()){
//                                var data = itr.next()
//                                UserConfig.shared.videoBuyer = data?.acf?.buyer_demo_video
//                                UserConfig.shared.videoArtisan = data?.acf?.artisan_demo_video
                            }
//                        }
//                    }
                }
            })
    }

    fun activateUser(id:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
//        Utility.displayMessage(token,Context)
        Log.d("Profile", "activateCalled")

        craftexchangemarketingRepository
            .getIndividualUserService()
            .activateUser(
                token ,
                id
            ).enqueue(object : Callback, retrofit2.Callback<UserStatusResponse> {
                override fun onFailure(call: Call<UserStatusResponse>, t: Throwable) {
                    t.printStackTrace()
                    activateListener?.onActivateFailure()
                }
                override fun onResponse(call: Call<UserStatusResponse>, response: Response<UserStatusResponse>) {
                    if (response?.isSuccessful) {
                        activateListener?.onActivateSuccess()
                    }
                }
            })
    }

    fun deactivateUser(id:Long){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
//        Utility.displayMessage(token,Context)

        craftexchangemarketingRepository
            .getIndividualUserService()
            .deactivateUser(
                token ,
                id
            ).enqueue(object : Callback, retrofit2.Callback<UserStatusResponse> {
                override fun onFailure(call: Call<UserStatusResponse>, t: Throwable) {
                    t.printStackTrace()
                    deactivateListener?.onDeactivateFailure()
                }
                override fun onResponse(call: Call<UserStatusResponse>, response: Response<UserStatusResponse>) {
                    if (response?.isSuccessful) {
                        deactivateListener?.onDeactivateSuccess()
                    }

                }
            })
    }
    fun setRating(id:Long, rating:Float?){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
//        Utility.displayMessage(rating.toString(),)
        Log.d("Profile33", "activateCalled" + rating)

        craftexchangemarketingRepository
            .getIndividualUserService()
            .setRating(
                token ,
                id,
                rating
            ).enqueue(object : Callback, retrofit2.Callback<UserStatusResponse> {
                override fun onFailure(call: Call<UserStatusResponse>, t: Throwable) {
                    t.printStackTrace()
//                    activateListener?.onActivateFailure()
                    setratingListener?.onRatingFailure()
                }
                override fun onResponse(call: Call<UserStatusResponse>, response: Response<UserStatusResponse>) {
                    if (response?.isSuccessful) {
//                        activateListener?.onActivateSuccess()
                        setratingListener?.onRatingSuccess()
                    }
                }
            })
    }
}