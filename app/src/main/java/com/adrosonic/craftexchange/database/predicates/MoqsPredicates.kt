package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.EditArtisanDetails
import com.adrosonic.craftexchange.repository.data.response.Notification.GetAllNotification
import com.adrosonic.craftexchange.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.login.BuyerResponse
import com.adrosonic.craftexchange.repository.data.response.moq.Data2
import com.adrosonic.craftexchange.repository.data.response.moq.Datum1
import com.adrosonic.craftexchange.repository.data.response.moq.Moq
import io.realm.Realm
import io.realm.RealmResults
import java.lang.Exception

class MoqsPredicates {
    companion object {
        private val TAG="MoqsPredicates"
        private var nextID : Long? = 0

        fun insertMoq(moq: Moq,accepted: Boolean,enquiryId:Long) {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                            var moqObj = realm.where(Moqs::class.java)
                                .equalTo(Moqs.COLUMN_MOQ_ID, moq.id)
                                .limit(1)
                                .findFirst()
                            val moqId=moqObj?.moqId?:0
                            Log.e("Moqs", "222222222222222 : $moqId")
                            if(moqId.equals(moq.id)){
                                Log.e("Moqs", "333333 Update: ${moq.id}")
                                moqObj?.accepted = accepted
                                moqObj?.enquiryId = enquiryId
                                moqObj?.moqId = moq.id
                                moqObj?.moq = moq.moq
                                moqObj?.ppu = moq.ppu
                                moqObj?.deliveryTimeId = moq.deliveryTimeId
                                moqObj?.isSend = moq.isSend
                                moqObj?.additionalInfo = moq.additionalInfo
                                moqObj?.createdOn = moq.createdOn
                                moqObj?.modifiedOn = moq.modifiedOn
                                realm.copyToRealmOrUpdate(moqObj)
                            }
                            else{
                                Log.e("Moqs", "4444444 Insert: ${moq.id}")
                                var primId = it.where(Moqs::class.java).max(Moqs.COLUMN__ID)
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var moqObj = it.createObject(Moqs::class.java, nextID)
                                moqObj?.accepted = accepted
                                moqObj?.enquiryId = enquiryId
                                moqObj?.moqId = moq.id
                                moqObj?.moq = moq.moq
                                moqObj?.ppu = moq.ppu
                                moqObj?.deliveryTimeId = moq.deliveryTimeId
                                moqObj?.isSend = moq.isSend
                                moqObj?.additionalInfo = moq.additionalInfo
                                moqObj?.createdOn = moq.createdOn
                                moqObj?.modifiedOn = moq.modifiedOn
                                realm.copyToRealmOrUpdate(moqObj)
                            }
                        }
            } catch (e: Exception) {
                Log.e("Notifications", "Insert Exception: ${e}")
            } finally {
//                realm.close()
            }
        }

        fun insertMoqs(moqsList:List<Datum1>?) {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var moqsIterator = moqsList?.iterator()
                    if (moqsIterator != null) {
                        while (moqsIterator.hasNext()) {
                            var moq = moqsIterator.next()
                            var moqObj = realm.where(Moqs::class.java)
                                .equalTo(Moqs.COLUMN_MOQ_ID, moq.moq.id)
                                .limit(1)
                                .findFirst()
                            val moqId = moqObj?.moqId ?: 0
                            Log.e("Moqs", "222222222222222 : $moqId")
                            if (moqId.equals(moq.moq.id)) {
                                Log.e("Moqs", "333333 Update: ${moq.moq.id}")
                                moqObj?.accepted = moq.accepted
                                moqObj?.enquiryId = moq.enquiryId
                                moqObj?.moqId = moq.moq.id
                                moqObj?.moq = moq.moq.moq
                                moqObj?.ppu = moq.moq.ppu
                                moqObj?.deliveryTimeId = moq.moq.deliveryTimeId
                                moqObj?.isSend = moq.moq.isSend
                                moqObj?.additionalInfo = moq.moq.additionalInfo
                                moqObj?.createdOn = moq.moq.createdOn
                                moqObj?.modifiedOn = moq.moq.modifiedOn
                                moqObj?.artisanId=moq?.artisanId
                                moqObj?.brand=moq?.brand
                                moqObj?.logo=moq?.logo
                                moqObj?.clusterName=moq?.clusterName
                                moqObj?.state=moq?.state
                                realm.copyToRealmOrUpdate(moqObj)
                            } else {
                                Log.e("Moqs", "4444444 Insert: ${moq.moq.id}")
                                var primId = it.where(Moqs::class.java).max(Moqs.COLUMN__ID)
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var moqObj = it.createObject(Moqs::class.java, nextID)
                                moqObj?.accepted = moq.accepted
                                moqObj?.enquiryId = moq.enquiryId
                                moqObj?.moqId = moq.moq.id
                                moqObj?.moq = moq.moq.moq
                                moqObj?.ppu = moq.moq.ppu
                                moqObj?.deliveryTimeId = moq.moq.deliveryTimeId
                                moqObj?.isSend = moq.moq.isSend
                                moqObj?.additionalInfo =moq. moq.additionalInfo
                                moqObj?.createdOn = moq.moq.createdOn
                                moqObj?.modifiedOn = moq.moq.modifiedOn
                                moqObj?.artisanId=moq?.artisanId
                                moqObj?.brand=moq?.brand
                                moqObj?.logo=moq?.logo
                                moqObj?.clusterName=moq?.clusterName
                                moqObj?.state=moq?.state
                                realm.copyToRealmOrUpdate(moqObj)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Notifications", "Insert Exception: ${e}")
            } finally {
//                realm.close()
            }
        }

        fun insertMoqForOffline(enquiryId: Long, additionalInfo:String, deliveryTimeId:Long, moq:Long, ppu:String){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    Log.e("Moqs", "Insert: ${enquiryId}")
                    var primId = it.where(Moqs::class.java).max(Moqs.COLUMN__ID)
                    if (primId == null) {
                        nextID = 1
                    } else {
                        nextID = primId.toLong() + 1
                    }
                    var moqObj = it.createObject(Moqs::class.java, nextID)
                    moqObj?.enquiryId = enquiryId
                    moqObj?.moq = moq
                    moqObj?.ppu = ppu
                    moqObj?.deliveryTimeId = deliveryTimeId
                    moqObj?.additionalInfo = additionalInfo
                    moqObj?.actionMarkMoqForSend = 1
                    Log.e("Moqs", "Insert nextID: ${nextID}")
                    realm.copyToRealmOrUpdate(moqObj)
                }
                } catch (e: Exception) {
                    Log.e("Moqs", "Insert Exception: ${e}")
                } finally {
//                realm.close()
                }
        }

        fun updateMoqsPostSend(moq: Moq,accepted: Boolean,enquiryId:Long,_id:Long){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var moqObj = realm.where(Moqs::class.java)
                        .equalTo(Moqs.COLUMN__ID, _id)
                        .limit(1)
                        .findFirst()
                    moqObj?.let {
                        Log.e("Moqs", "333333 Update: ${moq.id}")
                        moqObj?.accepted = accepted
                        moqObj?.enquiryId = enquiryId
                        moqObj?.moqId = moq.id
                        moqObj?.moq = moq.moq
                        moqObj?.ppu = moq.ppu
                        moqObj?.deliveryTimeId = moq.deliveryTimeId
                        moqObj?.isSend = moq.isSend
                        moqObj?.additionalInfo = moq.additionalInfo
                        moqObj?.createdOn = moq.createdOn
                        moqObj?.modifiedOn = moq.modifiedOn
                        moqObj?.actionMarkMoqForSend = 0
                        realm.copyToRealmOrUpdate(moqObj)
                    }
                }
            } catch (e: Exception) {
                Log.e("Notifications", "Insert Exception: ${e}")
            } finally {
//                realm.close()
            }
        }

        fun updateMoqsPostSelection(moq: Data2?, enquiryId:Long){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var moqObj = realm.where(Moqs::class.java)
                        .equalTo(Moqs.COLUMN_MOQ_ID, moq?.id)
                        .limit(1)
                        .findFirst()
                    Log.e(TAG, "Update: ${enquiryId}")
                    moqObj?.let {
                        Log.e(TAG, "333333 Update: ${moq?.id}")
                        moqObj?.accepted = false
                        moqObj?.enquiryId = enquiryId
                        moqObj?.moqId = moq?.id
                        moqObj?.moq = moq?.moq
                        moqObj?.ppu = moq?.ppu
                        moqObj?.deliveryTimeId = moq?.deliveryTimeID
                        moqObj?.isSend = moq?.isSend
                        moqObj?.additionalInfo = moq?.additionalInfo
                        moqObj?.createdOn = moq?.createdOn
                        moqObj?.modifiedOn = moq?.modifiedOn
                        moqObj?.actionMarkMoqForSend = 0
                        realm.copyToRealmOrUpdate(moqObj)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Update Exception: ${e}")
            } finally {
//                realm.close()
            }
        }

        fun deleteMoqs(enquiryId: Long,moqId:Long){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var moqObj = realm.where(Moqs::class.java)
                        .equalTo(Moqs.COLUMN_ENQUIRY_ID, enquiryId)
                        .findAll()
                    moqObj?.forEach {
                        Log.e(TAG, "Delete ${it.moqId} : $moqId")
                        it.deleteFromRealm()
//                        if(!it.moqId!!.equals(moqId)) it.deleteFromRealm()
                    }
                }
            }catch (e:Exception){
                Log.e(TAG, "Delete Exception: ${e}")
            }
        }

        fun getSingleMoq(enquiryId: Long?): Moqs? {
            var realm = CXRealmManager.getRealmInstance()
            var moq: Moqs? =null
            realm.executeTransaction {
                moq = realm.where(Moqs::class.java).equalTo(Moqs.COLUMN_ENQUIRY_ID, enquiryId).limit(1).findFirst()
            }
            return moq
        }

        fun getSingleMoqByMoqId(moqId: Long?): Moqs? {
            var realm = CXRealmManager.getRealmInstance()
            var moq: Moqs? =null
            realm.executeTransaction {
                moq = realm.where(Moqs::class.java).equalTo(Moqs.COLUMN_MOQ_ID, moqId).limit(1).findFirst()
            }
            return moq
        }

        fun getMoqs(enquiryId: Long?): RealmResults<Moqs>? {
            var realm = CXRealmManager.getRealmInstance()
            var moq: RealmResults<Moqs>? =null
            realm.executeTransaction {
                moq = realm.where(Moqs::class.java).equalTo(Moqs.COLUMN_ENQUIRY_ID, enquiryId).findAll()
            }
            return moq
        }

        fun getSingleMoqForOffline(_id: Long?): Moqs? {
            var realm = CXRealmManager.getRealmInstance()
            var moq: Moqs? =null
            realm.executeTransaction {
                moq = realm.where(Moqs::class.java).equalTo(Moqs.COLUMN__ID, _id).limit(1).findFirst()
            }
            return moq
        }

        fun getMoqMarkedForActions(actionsMarked:String): ArrayList<Long>? {
            var realm = CXRealmManager.getRealmInstance()
            var itemId=ArrayList<Long>()
            try {
                realm.executeTransaction {
                    var message= when(actionsMarked){
                        "actionMarkMoqForSend=1"-> {realm.where(Moqs::class.java)
                            .equalTo(Moqs.COLUMN_MARK_MOQ_FOR_SENDING,1L)
                            .findAll()
                        }
                        else-> null
                    }
                    if(message!=null){
                        val iterator=message.iterator()
                        while (iterator.hasNext()) {
                            itemId.add(iterator.next()._id?:0L)
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

        fun getMinPpu(enquiryId:Long):Int{
            var realm = CXRealmManager.getRealmInstance()
            var minPpu: Int? =null
            realm.executeTransaction {
//                minPpu = realm.where(Moqs::class.java).equalTo(Moqs.COLUMN_ENQUIRY_ID, enquiryId).findAll().min(Moqs.COLUMN_PPU)?.toInt()
            }
            return 0//minPpu?:0
        }
        fun getMinEta(enquiryId:Long):Int{
            var realm = CXRealmManager.getRealmInstance()
            var minEta: Int? =null
            realm.executeTransaction {
                minEta = realm.where(Moqs::class.java).equalTo(Moqs.COLUMN_ENQUIRY_ID, enquiryId).findAll().min(Moqs.COLUMN_DELIVERY_TIME_ID)?.toInt()
            }
            return minEta?:0
        }
        fun getMinQty(enquiryId:Long):Int{
            var realm = CXRealmManager.getRealmInstance()
            var minEta: Int? =null
            realm.executeTransaction {
                minEta = realm.where(Moqs::class.java).equalTo(Moqs.COLUMN_ENQUIRY_ID, enquiryId).findAll().min(Moqs.COLUMN_MOQ)?.toInt()
            }
            return minEta?:0
        }
    }
}