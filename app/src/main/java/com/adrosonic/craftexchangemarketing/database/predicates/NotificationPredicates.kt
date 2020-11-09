package com.adrosonic.craftexchangemarketing.database.predicates

import android.util.Log
import com.adrosonic.craftexchangemarketing.database.CXRealmManager
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.UserAddress
import com.adrosonic.craftexchangemarketing.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.request.editProfileModel.EditArtisanDetails
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.GetAllNotification
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.login.BuyerResponse
import io.realm.Realm
import io.realm.RealmResults
import java.lang.Exception

class NotificationPredicates {
    companion object {
        private var nextID : Long? = 0

        fun insertNotification(notifications: List<GetAllNotification>?) {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var notificationsList =notifications
            try {
                realm.executeTransaction {
                    var notificationsIterator = notificationsList?.iterator()
                    if (notificationsIterator != null) {
                        Log.e("Notifications", "11111111111111")
                        while (notificationsIterator.hasNext()) {
                            var notification = notificationsIterator.next()
                            var notificationObj = realm.where(Notifications::class.java)
                                .equalTo(Notifications.COLUMN_NOTIFICATION_ID, notification.notificationId)
                                .limit(1)
                                .findFirst()
                            val notificationid=notificationObj?.notificationId?:0
                            Log.e("Notifications", "222222222222222 : $notificationid")
                            if(notificationid.equals(notification.notificationId)){
                                Log.e("Notifications", "333333 Update: ${notification.notificationId}")
                                notificationObj?.code = notification.code?:""
                                notificationObj?.createdOn = notification.createdOn?:""
//                                notificationObj?.customProduct = notification.customProduct?:""
                                notificationObj?.notificationId = notification.notificationId?:0
//                                notificationObj?.notificationTypeId = notification.notificationTypeId?:0
                                notificationObj?.productDesc = notification.productDesc?:""
                                notificationObj?.notificationType = notification.notificationType
//                                notificationObj?.seen = notification.seen?:0
//                                notificationObj?.type = notification.type?:""
                                realm.copyToRealmOrUpdate(notificationObj)
                            }
                            else{
                                Log.e("Notifications", "4444444 Insert: ${notification.notificationId}")
                                var primId = it.where(Notifications::class.java).max(Notifications.COLUMN__ID)
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var noti = it.createObject(Notifications::class.java, nextID)
                                notificationObj?.notificationType = notification.notificationType
                                noti?.code = notification.code?:""
                                noti?.createdOn = notification.createdOn?:""
                                noti.notificationId = notification.notificationId?:0
                                noti.productDesc = notification.productDesc?:""
                                realm.copyToRealmOrUpdate(noti)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Notifications", "Insert Exception: ${e}")
            } finally {
//                realm.close()
            }
            setSeen(notifications)
        }

        fun setSeen(notifications: List<GetAllNotification>?){
            val realm = CXRealmManager.getRealmInstance()
            var notificationsRealm: RealmResults<Notifications>? = null
            var idList=ArrayList<Long>()
            try {
                notifications?.forEach {
                    idList.add(it.notificationId)
                }
                realm?.executeTransaction {
                    notificationsRealm= realm.where(Notifications::class.java).findAll()
                    notificationsRealm?.forEach {
                        if(!idList.contains(it.notificationId)){
                            //todo delete
                            val deletNoti=realm.where(Notifications::class.java).equalTo(Notifications.COLUMN_NOTIFICATION_ID,it.notificationId).findAll()
                            deletNoti.deleteAllFromRealm()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Notifications", " Exception: ${e}")
            } finally {
//            realm.close()
            }
        }

        fun getAllNotifications():RealmResults<Notifications>? {
            val realm = CXRealmManager.getRealmInstance()
            var notifications: RealmResults<Notifications>? = null
            try {
                realm?.executeTransaction {
                    notifications = realm.where(Notifications::class.java).notEqualTo(Notifications.COLUMN_ACTION_MARK_READ,1L).findAll()
                }
            } catch (e: Exception) {
                Log.e("Notifications", " Exception: ${e}")
            } finally {
//            realm.close()
            }
            return notifications
        }

        fun getNotificationMarkedForActions(actionsMarked:String): ArrayList<Long>? {
            var realm = CXRealmManager.getRealmInstance()
            var itemId=ArrayList<Long>()
            try {
                realm.executeTransaction {
                    var message= when(actionsMarked){
                        "actionMarkRead=1"-> {realm.where(Notifications::class.java)
                            .equalTo(Notifications.COLUMN_ACTION_MARK_READ,1L)
                            .findAll()
                        }
                        else-> null
                    }
                    if(message!=null){
                        val iterator=message.iterator()
                        while (iterator.hasNext()) {
                            itemId.add(iterator.next().notificationId?:0L)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("offline","while fetching actions : "+e.message)
            } finally {
                realm.close()
            }
            return itemId
        }

        fun updateNoificationForRead(notificationId: Long?){
            var realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction{
                var product = realm.where(Notifications::class.java).equalTo(Notifications.COLUMN_NOTIFICATION_ID,notificationId).limit(1).findFirst()
                product?.actionMarkRead = 1
            }
        }
        fun updateNoificationPostRead(notificationId: Long?){
            var realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction{
                var product = realm.where(Notifications::class.java).equalTo(Notifications.COLUMN_NOTIFICATION_ID,notificationId).limit(1).findFirst()
                product?.deleteFromRealm()
            }
        }
        fun deleteAllNotifications(){
            var realm = CXRealmManager.getRealmInstance()
            realm.executeTransaction{
                var product = realm.where(Notifications::class.java).findAll()
                product?.deleteAllFromRealm()
            }
        }
    }
}