package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.entities.realmEntities.UserAddress
import com.adrosonic.craftexchange.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.EditArtisanDetails
import com.adrosonic.craftexchange.repository.data.response.Notification.GetAllNotification
import com.adrosonic.craftexchange.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.login.BuyerResponse
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
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
                        while (notificationsIterator.hasNext()) {
                            var notification = notificationsIterator.next()
                            var notificationObj = realm.where(Notifications::class.java)
                                .equalTo(Notifications.COLUMN_NOTIFICATION_ID, notification.notificationId)
                                .limit(1)
                                .findFirst()
                            val notificationid=notificationObj?.notificationId?:0
                            if(notificationid.equals(notification.notificationId)){
                                notificationObj?.code = notification.code?:""
                                notificationObj?.companyName = notification.companyName?:""
                                notificationObj?.createdOn = notification.createdOn?:""
                                notificationObj?.customProduct = notification.customProduct?:""
                                notificationObj?.notificationId = notification.notificationId?:0
                                notificationObj?.notificationTypeId = notification.notificationTypeId?:0
                                notificationObj?.productDesc = notification.productDesc?:""
                                notificationObj?.seen = notification.seen?:0
                                notificationObj?.type = notification.type?:""
                                realm.copyToRealmOrUpdate(notificationObj)
                            }
                            else{
                                var primId = it.where(Notifications::class.java).max(Notifications.COLUMN__ID)
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var noti = it.createObject(Notifications::class.java, nextID)
                                noti.code = notification.code?:""
                                noti.companyName = notification.companyName?:""
                                noti?.createdOn = notification.createdOn?:""
                                noti.customProduct = notification.customProduct?:""
                                noti.notificationId = notification.notificationId?:0
                                noti.notificationTypeId = notification.notificationTypeId?:0
                                noti.productDesc = notification.productDesc?:""
                                noti.seen = notification.seen?:0
                                noti.type = notification.type?:""
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
                    notifications = realm.where(Notifications::class.java)
                        .notEqualTo(Notifications.COLUMN_ACTION_MARK_READ,1L)
                        .sort(Notifications.COLUMN_CREATED_ON,Sort.DESCENDING)
                        .findAll()
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