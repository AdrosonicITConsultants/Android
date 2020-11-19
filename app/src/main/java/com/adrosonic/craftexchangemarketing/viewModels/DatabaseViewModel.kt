package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.admin.database.UserDataRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseCountResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.DatabaseResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {

    interface DbInterface{
        fun onSuccess(userList: List<User>)
        fun onFailure()
        fun onCountSuccess(count:Int)
        fun onCountFailure()
    }
    var listener: DbInterface? = null
    var page=0
    var userList=ArrayList<User>()
    fun getDatabaseForAdmin(isFilter:Boolean,clusterId : Int,  pageNo:Int, rating:Int, roleId:Int,
                             searchStr:String?, sortBy : String,sortType : String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        page=pageNo
        craftexchangemarketingRepository
            .getUserDatabaseService()
            .getUserData(token, UserDataRequest(clusterId , pageNo, rating , roleId , searchStr , sortBy ,sortType ))
            .enqueue(object : Callback, retrofit2.Callback<DatabaseResponse> {
                override fun onFailure(call: Call<DatabaseResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("DatabaseResults","Failure :"+t.message)
                    listener?.onFailure()
                }
                override fun onResponse(
                    call: Call<DatabaseResponse>,
                    response: Response<DatabaseResponse>
                ) {
                    if(response.body()?.data != null) {
                        Log.e("DatabaseResults", "Success : $isFilter")
                        if(isFilter) {
                            Log.e("DatabaseResults", "Success : " + response?.body()?.data?.size+": $page")
                            listener?.onSuccess(response?.body()?.data!!)
                        }
                        else{
                            Log.e("DatabaseResults", "page : $page :: ${UserConfig.shared.artisanDbPageCount}")
                            page++
                            if(page>UserConfig.shared.artisanDbPageCount){
                                listener?.onSuccess(userList)
                            }
                            else {
                                userList.addAll(response?.body()?.data!!)
                                getDatabaseForAdmin(isFilter,clusterId, page, rating, roleId,searchStr, sortBy,sortType)
                            }
                        }
                    }else{
                        Log.e("DatabaseResults","Failure")
                        listener?.onFailure()
                    }
                }
            })
    }

    fun getDatabaseCountForAdmin(clusterId : Int, pageNo:Int, rating:Int, roleId:Int,
                            searchStr:String?, sortBy : String,sortType : String){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getUserDatabaseService()
            .getUserDataCount(token, UserDataRequest(clusterId , pageNo, rating , roleId , searchStr , sortBy ,sortType ))
            .enqueue(object : Callback, retrofit2.Callback<DatabaseCountResponse> {
                override fun onFailure(call: Call<DatabaseCountResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("AdminDatabaseCount","Failure :"+t.printStackTrace().toString())
                    listener?.onCountFailure()
                }
                override fun onResponse(
                    call: Call<DatabaseCountResponse>,
                    response: Response<DatabaseCountResponse>
                ) {
                    if(response.body()?.data != null) {
                        Log.e("AdminDatabaseCount", "Success : " + response?.body()?.data)

                       var quotient = (response?.body()?.data?:0) / 12
                        var remainder = (response?.body()?.data?:0 )% 12
                        if(remainder>0) quotient++
                        Log.e("AdminDatabaseCount", "quotient : $quotient")
                        UserConfig.shared.artisanDbPageCount=quotient
                        listener?.onCountSuccess(response.body()?.data?:0)
                    }else{
                        Log.e("AdminDatabaseCount","Failure")
                        listener?.onCountFailure()
                    }
                }
            })
    }
}