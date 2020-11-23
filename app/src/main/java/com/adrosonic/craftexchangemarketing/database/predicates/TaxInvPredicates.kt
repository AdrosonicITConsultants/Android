package com.adrosonic.craftexchangemarketing.database.predicates

import android.util.Log
import com.adrosonic.craftexchangemarketing.database.CXRealmManager
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.QcDetails
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.TaxInvDetails
import com.adrosonic.craftexchangemarketing.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchangemarketing.repository.data.request.taxInv.SendTiRequest
import com.adrosonic.craftexchangemarketing.repository.data.response.pi.SendPiResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.taxInv.TaxInvoiceResponse
import java.lang.Exception


class TaxInvPredicates {
    companion object {
        private val TAG = "TiPredicates"
        private var nextID: Long? = 0

        fun insertTiForOffline(enquiryId: Long, send:Long,sendTiRequest: SendTiRequest){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    //
                    var tiObj = realm.where(TaxInvDetails::class.java)
                        .equalTo(TaxInvDetails.COLUMN_ENQUIRY_ID, enquiryId)
                        .limit(1)
                        .findFirst()
                    val enqId=tiObj?.enquiryID?:0
                    Log.e(TAG, "222222222222222 : $enqId")
                    if(enqId != enquiryId) {
                        Log.e(TAG, "Insert: $enquiryId")
                        var primId = it.where(TaxInvDetails::class.java).max(TaxInvDetails.COLUMN__ID)
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var tiDetails = it.createObject(TaxInvDetails::class.java, nextID)
                        tiDetails?.enquiryID = enquiryId
                        tiDetails?.quantity = sendTiRequest?.quantity
                        tiDetails?.ppu = sendTiRequest?.ppu
                        tiDetails?.advancePaidAmt = sendTiRequest?.advancePaidAmt?.toLong()
                        tiDetails?.deliveryCharges = sendTiRequest?.deliveryCharges?.toLong()
                        tiDetails?.cgst = sendTiRequest?.cgst?.toLong()
                        tiDetails?.sgst = sendTiRequest?.sgst?.toLong()
                        tiDetails?.finalTotalAmt = sendTiRequest?.finalTotalAmt

                        tiDetails?.actionMarkTiForSend = send

                        Log.e(TAG, "Insert nextID: $nextID")
                        realm.copyToRealmOrUpdate(tiDetails)
                    } else{

                        tiObj?.enquiryID = enquiryId
                        tiObj?.quantity = sendTiRequest?.quantity
                        tiObj?.ppu = sendTiRequest?.ppu
                        tiObj?.advancePaidAmt = sendTiRequest?.advancePaidAmt?.toLong()
                        tiObj?.deliveryCharges = sendTiRequest?.deliveryCharges?.toLong()
                        tiObj?.cgst = sendTiRequest?.cgst?.toLong()
                        tiObj?.sgst = sendTiRequest?.sgst?.toLong()
                        tiObj?.finalTotalAmt = sendTiRequest?.finalTotalAmt

                        tiObj?.actionMarkTiForSend = send
                        Log.e(TAG, "Update enquiryId: $enquiryId")
                        realm.copyToRealmOrUpdate(tiObj)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "InsertUpdate Exception: $e")
            } finally {
//                realm.close()
            }
        }

        fun insertTi(taxInv: TaxInvoiceResponse){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            try {
                realm.executeTransaction {
                    val tiOb=taxInv.data
                    var tiObj = realm.where(TaxInvDetails::class.java)
                        .equalTo(TaxInvDetails.COLUMN_ENQUIRY_ID, tiOb?.enquiryId)
                        .limit(1)
                        .findFirst()
                    val enqId=tiObj?.enquiryID?:0

                    if(enqId != tiOb?.enquiryId) {
                        var primId = it.where(TaxInvDetails::class.java).max(TaxInvDetails.COLUMN__ID)
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var tiDetails = it.createObject(TaxInvDetails::class.java, nextID)
                        tiDetails?.enquiryID = tiOb?.enquiryId
                        tiDetails?.actionMarkTiForSend = 0
                        tiDetails?.taxInvID = tiOb?.id
                        tiDetails?.hsn = tiOb?.hsn
                        tiDetails?.ppu = tiOb?.ppu
                        tiDetails?.quantity = tiOb?.quantity
                        tiDetails?.piAmt = tiOb?.piAmt
                        tiDetails?.date = tiOb?.date
                        tiDetails?.invoiceNo = tiOb?.invoiceNo
                        tiDetails?.artisanID = tiOb?.artisanId
                        tiDetails?.advancePaidAmt = tiOb?.advancePaidAmt
                        tiDetails?.deliveryCharges = tiOb?.deliveryCharges
                        tiDetails?.cgst = tiOb?.cgst
                        tiDetails?.sgst = tiOb?.sgst
                        tiDetails?.finalTotalAmt = tiOb?.finalTotalAmt
                        tiDetails?.isactive = tiOb?.isactive
                        tiDetails?.createdon = tiOb?.createdon
                        tiDetails?.modifiedon = tiOb?.modifiedon

                        realm.copyToRealmOrUpdate(tiDetails)
                    } else{
                        tiObj?.enquiryID =  tiOb?.enquiryId
                        tiObj?.actionMarkTiForSend = 0
                        tiObj?.taxInvID = tiOb?.id
                        tiObj?.hsn = tiOb?.hsn
                        tiObj?.ppu = tiOb?.ppu
                        tiObj?.quantity = tiOb?.quantity
                        tiObj?.piAmt = tiOb?.piAmt
                        tiObj?.date = tiOb?.date
                        tiObj?.invoiceNo = tiOb?.invoiceNo
                        tiObj?.artisanID = tiOb?.artisanId
                        tiObj?.advancePaidAmt = tiOb?.advancePaidAmt
                        tiObj?.deliveryCharges = tiOb?.deliveryCharges
                        tiObj?.cgst = tiOb?.cgst
                        tiObj?.sgst = tiOb?.sgst
                        tiObj?.finalTotalAmt = tiOb?.finalTotalAmt
                        tiObj?.isactive = tiOb?.isactive
                        tiObj?.createdon = tiOb?.createdon
                        tiObj?.modifiedon = tiOb?.modifiedon


                        realm.copyToRealmOrUpdate(tiObj)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "InsertUpdate Exception: ${e}")
            } finally {
//                realm.close()
            }
        }

        fun getTiMarkedForActions(actionsMarked: String): ArrayList<Long>? {
            var realm = CXRealmManager.getRealmInstance()
            var itemId = ArrayList<Long>()
            try {
                realm.executeTransaction {
                    var message1 = realm.where(TaxInvDetails::class.java)
                        .equalTo(TaxInvDetails.COLUMN_MARK_TI_FOR_SENDING, 1L)
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
                    Log.e(TAG, "message1 :${message1?.count()}")
                    if (message1 != null) {
                        val iterator = message1.iterator()
                        while (iterator.hasNext()) {
                            val id = iterator.next()._id ?: 0L
                            Log.e(TAG, "itemId :${id}")
                            itemId.add(id)
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e("${TAG} offline", "while fetching actions : " + e.message)
            } finally {
//                realm.close()
            }
            return itemId
        }

        fun getTiById(_id: Long?): TaxInvDetails? {
            var realm = CXRealmManager.getRealmInstance()
            var tiDetails: TaxInvDetails? =null
            realm.executeTransaction {
                tiDetails = realm.where(TaxInvDetails::class.java).equalTo(TaxInvDetails.COLUMN__ID, _id).limit(1).findFirst()
            }
            return tiDetails
        }

    }
}