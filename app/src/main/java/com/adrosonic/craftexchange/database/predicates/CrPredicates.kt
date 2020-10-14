package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.changeReequest.CrDetailsResponse
import io.realm.RealmResults
import java.lang.Exception

class CrPredicates {
    companion object {
        private val TAG="CrPredicates"
        private var nextID : Long? = 0

        fun insertChangeReq(crDetailst: CrDetailsResponse?){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    val crIterator=crDetailst?.data?.changeRequestItemList?.iterator()
                    while (crIterator!!.hasNext()) {
                        val crItemsOb=crIterator.next()
                        var crObj = realm.where(ChangeRequests::class.java)
                            .equalTo(ChangeRequests.COLUMN_CR_ID,crItemsOb?.id)
                            .limit(1)
                            .findFirst()
                        val crId = crObj?.crId ?: 0
                        Log.e("CrDetails", "11111111: ${crItemsOb.id}")
                        if (!crId.equals(crItemsOb.id)) {
                            Log.e("CrDetails", "Insert: ${crItemsOb.id}")
                            var primId = it.where(ChangeRequests::class.java).max(ChangeRequests.COLUMN__ID)
                            if (primId == null) {
                                nextID = 1
                            } else {
                                nextID = primId.toLong() + 1
                            }
                            var crDetails = it.createObject(ChangeRequests::class.java, nextID)
                            crDetails?.enquiryId = crDetailst.data.changeRequest.enquiryId
                            crDetails?.crId = crItemsOb.id
                            crDetails?.status = crDetailst.data.changeRequest.status
                            crDetails?.createdOn = crDetailst.data.changeRequest.createdOn
                            crDetails?.modifiedOn =crDetailst.data.changeRequest.modifiedOn
                            crDetails?.changeRequestId = crItemsOb?.changeRequestId
                            crDetails?.requestItemsId = crItemsOb?.requestItemsId
                            crDetails?.requestText = crItemsOb?.requestText
                            crDetails?.requestStatus = crItemsOb?.requestStatus
                            Log.e("CrDetails", "Insert nextID: ${nextID}")
                            realm.copyToRealmOrUpdate(crDetails)
                        } else {
                            Log.e("CrDetails", "Update: ${crItemsOb.id}")
                            crObj?.enquiryId = crDetailst.data.changeRequest.enquiryId
                            crObj?.crId = crItemsOb.id
                            crObj?.status = crDetailst.data.changeRequest.status
                            crObj?.createdOn = crDetailst.data.changeRequest.createdOn
                            crObj?.modifiedOn =crDetailst.data.changeRequest.modifiedOn
                            crObj?.changeRequestId = crItemsOb?.changeRequestId
                            crObj?.requestItemsId = crItemsOb?.requestItemsId
                            crObj?.requestText = crItemsOb?.requestText
                            crObj?.requestStatus = crItemsOb?.requestStatus
                            Log.e("CrDetails", "Update enquiryId: ${ crDetailst.data.changeRequest.enquiryId}")
                            realm.copyToRealmOrUpdate(crObj)
                        }
                    }
                }
                } catch (e: Exception) {
                    Log.e(TAG, "InsertUpdate Exception: ${e}")
                } finally {
//                realm.close()
                }
        }

        fun getCrs(enquiryId: Long?): RealmResults<ChangeRequests>? {
            var realm = CXRealmManager.getRealmInstance()
            var crDetails: RealmResults<ChangeRequests>? =null
            realm.executeTransaction {
                crDetails = realm.where(ChangeRequests::class.java).
                equalTo(ChangeRequests.COLUMN_ENQUIRY_ID, enquiryId).findAll()
            }
            return crDetails
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