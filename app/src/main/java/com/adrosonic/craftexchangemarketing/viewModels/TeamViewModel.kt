package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.team.AdminsRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminRolesResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminsResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class TeamViewModel(application: Application) : AndroidViewModel(application) {

    interface AdminDetailsInterface{
        fun onSuccessAdminDetails()
        fun onFailureAdminDetails()
    }
    var adminListener: AdminDetailsInterface? = null

    var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

    fun getTeamList(request : AdminsRequest){
        craftexchangemarketingRepository
            .getTeamService()
            .getAdmins(token, request)
            .enqueue(object : Callback, retrofit2.Callback<AdminsResponse> {
                override fun onFailure(call: Call<AdminsResponse>, t: Throwable) {
                    t.printStackTrace()
                    adminListener?.onFailureAdminDetails()
                }
                override fun onResponse(call: Call<AdminsResponse>, response: Response<AdminsResponse>) {
                    if (response?.body()?.valid == true){
                        adminListener?.onSuccessAdminDetails()
                        if(response?.body()?.data != null){
                            UserConfig.shared.adminTeam=Gson().toJson(response.body())
                        }
                    }else{
                        adminListener?.onFailureAdminDetails()
                    }
                }
            })
    }

    fun getAdminProfile(adminId : Int){
        craftexchangemarketingRepository
            .getTeamService()
            .getAdmin(token,adminId)
            .enqueue(object : Callback, retrofit2.Callback<AdminResponse> {
                override fun onFailure(call: Call<AdminResponse>, t: Throwable) {
                    t.printStackTrace()
                    adminListener?.onFailureAdminDetails()
                }
                override fun onResponse(call: Call<AdminResponse>, response: Response<AdminResponse>) {
                    if (response?.body()?.valid == true){
                        adminListener?.onSuccessAdminDetails()
                        UserConfig.shared.adminProfile=Gson().toJson(response.body())
                    }else{
                        adminListener?.onFailureAdminDetails()
                    }
                }
            })
    }
}