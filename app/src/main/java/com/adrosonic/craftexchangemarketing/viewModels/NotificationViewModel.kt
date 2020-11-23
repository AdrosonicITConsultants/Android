package com.adrosonic.craftexchangemarketing.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.predicates.NotificationPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.WishlistPredicates
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.NotificationResponse
import com.adrosonic.craftexchangemarketing.services.notification.NotificationReadService
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import okhttp3.ResponseBody
import retrofit2.Call
import javax.security.auth.callback.Callback

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    interface NotificationInterface{
        fun onSuccess()
        fun onFailure()
    }
    var listener: NotificationInterface? = null

    val NotificationData : MutableLiveData<RealmResults<Notifications>> by lazy { MutableLiveData<RealmResults<Notifications>>() }

    fun getNotificationsMutableData(): MutableLiveData<RealmResults<Notifications>> {
        NotificationData.value=loadNotificationsData()
        return NotificationData
    }
    fun loadNotificationsData(): RealmResults<Notifications> {
        var notifications= NotificationPredicates.getAllNotifications()
        Log.e("notifications","notifications :"+notifications?.size)
        return notifications!!
    }
    fun getAllNotifications(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getNotificationService()
            .getAllAdminNotifications(token)
            .enqueue(object: Callback, retrofit2.Callback<NotificationResponse> {
                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e(TAG,"onFailure: "+t.message)
                    listener?.onFailure()
                }
                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: retrofit2.Response<NotificationResponse>) {
                    if(response.body()?.valid == true){
                        Log.e(TAG,"getProductUploadData :"+response.body()?.data?.count)
                        NotificationPredicates.insertNotification(response.body()?.data?.getAllNotifications)
                        UserConfig.shared?.notiBadgeCount=response.body()?.data?.count?:0
                        listener?.onSuccess()
                    }else{
                        Log.e(TAG,"getProductUploadData onFailure: "+response.body()?.errorCode)
                        listener?.onFailure()
                    }
                }
            })
    }

    fun markAllNotificationsAsRead(){
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        craftexchangemarketingRepository
            .getNotificationService()
            .markAllNotificationsAsRead(token)
            .enqueue(object: Callback, retrofit2.Callback<NotificationReadResponse> {
                override fun onFailure(call: Call<NotificationReadResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e(TAG,"onFailure: "+t.message)
                    listener?.onFailure()
                }
                override fun onResponse(
                    call: Call<NotificationReadResponse>,
                    response: retrofit2.Response<NotificationReadResponse>) {
                    Log.e(TAG,"getProductUploadData onResponse: "+response.body()?.valid)
                    if(response.body()?.valid == true){
                        NotificationPredicates.deleteAllNotifications()
                        listener?.onSuccess()
                    }else   listener?.onFailure()
                }
            })
    }
    companion object{
        const val TAG = "NotificationVm"
    }
}