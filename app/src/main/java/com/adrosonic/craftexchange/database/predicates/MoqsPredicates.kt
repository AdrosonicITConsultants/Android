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
import com.adrosonic.craftexchange.repository.data.response.moq.Moq
import io.realm.Realm
import io.realm.RealmResults
import java.lang.Exception

class MoqsPredicates {
    companion object {
        private var nextID : Long? = 0

        fun insertMoqs(moq: Moq,accepted: Boolean,enquiryId:Long) {
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

        fun getSingleMoq(enquiryId: Long?): Moqs? {
            var realm = CXRealmManager.getRealmInstance()
            var moq: Moqs? =null
            realm.executeTransaction {
                moq = realm.where(Moqs::class.java).equalTo(Moqs.COLUMN_ENQUIRY_ID, enquiryId).limit(1).findFirst()
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

    }
}