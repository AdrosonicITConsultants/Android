package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import java.lang.Exception

class PiPredicates {
    companion object {
        private val TAG="PiPredicates"
        private var nextID : Long? = 0

        fun insertPiForOffline(enquiryId: Long, send:Long,save:Long,sendPiRequest: SendPiRequest){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    //
                    var piObj = realm.where(PiDetails::class.java)
                        .equalTo(PiDetails.COLUMN_ENQUIRY_ID, enquiryId)
                        .limit(1)
                        .findFirst()
                    val enqId=piObj?.enquiryId?:0
                    Log.e(TAG, "222222222222222 : $enqId")
                    if(!enqId.equals(enquiryId)) {
                        Log.e(TAG, "Insert: ${enquiryId}")
                        var primId = it.where(PiDetails::class.java).max(PiDetails.COLUMN__ID)
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var piDetails = it.createObject(PiDetails::class.java, nextID)
                        piDetails?.enquiryId = enquiryId
                        piDetails?.actionMarkPiForSave = save
                        piDetails?.actionMarkPiForSend = send
                        piDetails?.cgst = sendPiRequest?.cgst
                        piDetails?.sgst = sendPiRequest?.sgst
                        piDetails?.hsn = sendPiRequest?.hsn
                        piDetails?.ppu = sendPiRequest?.ppu
                        piDetails?.quantity = sendPiRequest?.quantity
                        piDetails?.date = sendPiRequest?.expectedDateOfDelivery
                        Log.e(TAG, "Insert nextID: ${nextID}")
                        realm.copyToRealmOrUpdate(piDetails)
                    } else{
                        Log.e(TAG, "4444444 update: ${sendPiRequest.expectedDateOfDelivery}")
                        piObj?.enquiryId = enquiryId
                        piObj?.actionMarkPiForSave = save
                        piObj?.actionMarkPiForSend = send
                        piObj?.cgst = sendPiRequest?.cgst
                        piObj?.sgst = sendPiRequest?.sgst
                        piObj?.hsn = sendPiRequest?.hsn
                        piObj?.ppu = sendPiRequest?.ppu
                        piObj?.quantity = sendPiRequest?.quantity
                        piObj?.date = sendPiRequest?.expectedDateOfDelivery
                        Log.e(TAG, "Update enquiryId: ${enquiryId}")
                        realm.copyToRealmOrUpdate(piObj)
                    }
                }
                } catch (e: Exception) {
                    Log.e(TAG, "InsertUpdate Exception: ${e}")
                } finally {
//                realm.close()
                }
        }

        fun updatePiPostSelection( enquiryId:Long,save:Long?,send:Long?){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var piObj = realm.where(PiDetails::class.java)
                        .equalTo(PiDetails.COLUMN_ENQUIRY_ID, enquiryId)
                        .limit(1)
                        .findFirst()
                    Log.e(TAG, "Update: ${enquiryId}")
                    piObj?.let {
                        Log.e(TAG, "333333 Update: ${piObj?.date}")
                        save?.let { piObj?.actionMarkPiForSend = it }
                        send?.let {  piObj?.actionMarkPiForSave = it}
                        realm.copyToRealmOrUpdate(piObj)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Update Exception: ${e}")
            } finally {
//                realm.close()
            }
        }

        fun deletePi(_id:Long){
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    var piObj = realm.where(PiDetails::class.java)
                        .equalTo(PiDetails.COLUMN__ID, _id)
                        .findAll()
                    piObj.deleteAllFromRealm()
                }
            }catch (e:Exception){
                Log.e(TAG, "Delete Exception: ${e}")
            }
        }

        fun getSinglePi(enquiryId: Long?): PiDetails? {
            var realm = CXRealmManager.getRealmInstance()
            var piDetails: PiDetails? =null
            realm.executeTransaction {
                piDetails = realm.where(PiDetails::class.java).equalTo(PiDetails.COLUMN_ENQUIRY_ID, enquiryId).limit(1).findFirst()
            }
            return piDetails
        }

        fun getPiById(_id: Long?): PiDetails? {
            var realm = CXRealmManager.getRealmInstance()
            var piDetails: PiDetails? =null
            realm.executeTransaction {
                piDetails = realm.where(PiDetails::class.java).equalTo(PiDetails.COLUMN__ID, _id).limit(1).findFirst()
            }
            return piDetails
        }

        fun getPiMarkedForActions(actionsMarked:String): ArrayList<Long>? {
            var realm = CXRealmManager.getRealmInstance()
            var itemId=ArrayList<Long>()
            try {
                realm.executeTransaction {
                    var message1 =realm.where(PiDetails::class.java)
                        .equalTo(PiDetails.COLUMN_MARK_PI_FOR_SAVE,1L).or()
                        .equalTo(PiDetails.COLUMN_MARK_PI_FOR_SENDING,1L)
                        .findAll()
//                    var message= when(actionsMarked){
//                        "actionMarkPiForSave=1"-> {realm.where(PiDetails::class.java)
//                            .equalTo(PiDetails.COLUMN_MARK_PI_FOR_SAVE,1L)
//                            .findAll()
//                        }
//                        "actionMarkPiForSend=1"-> {realm.where(PiDetails::class.java)
//                            .equalTo(PiDetails.COLUMN_MARK_PI_FOR_SENDING,1L)
//                            .findAll()
//                        }
//                        else-> null
//                    }
                    Log.e(TAG,"message1 :${message1?.count()}")
                    if(message1!=null){
                        val iterator=message1.iterator()
                        while (iterator.hasNext()) {
                            val id=iterator.next()._id?:0L
                            Log.e(TAG,"itemId :${id}")
                            itemId.add(id)
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e("$TAG offline","while fetching actions : "+e.message)
            } finally {
//                realm.close()
            }
            return itemId
        }

    }
}