package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.PiDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.QcDetails
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.request.qc.QuestionAnswer
import com.adrosonic.craftexchange.repository.data.request.qc.SaveOrSendQcRequest
import com.adrosonic.craftexchange.repository.data.response.qc.BuyerQcResponse
import com.adrosonic.craftexchange.repository.data.response.qc.QCData
import com.adrosonic.craftexchange.repository.data.response.qc.QcResponse
import com.adrosonic.craftexchange.repository.data.response.qc.SaveSendQcResponse
import com.google.gson.Gson
import io.realm.RealmResults
import java.lang.Exception

class QcPredicates {
    companion object {
        private val TAG = "QcPredicates"
        private var nextID: Long? = 0

        fun insertArtisanQcResponses(qcResponse: QCData, enquiryId: Long) {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var qcObj: QcDetails? = null
            var responseString:String?=""
            try {
                realm.executeTransaction {

                    qcObj = realm?.where(QcDetails::class.java)
                        ?.equalTo(QcDetails?.COLUMN_ENQUIRY_ID, enquiryId)
//                        ?.and()
//                        ?.equalTo(QcDetails?.COLUMN_STAGE_ID, qcData?.stageId)
//                        ?.and()
//                        ?.equalTo(QcDetails?.COLUMN_QUESTION_ID, qcData?.questionId)
                        ?.limit(1)
                        ?.findFirst()

                    if (qcResponse != null){
                        responseString = Gson()?.toJson(qcResponse)
                    }

                    if (qcObj == null) {
                        var primId = it.where(QcDetails::class.java).max(QcDetails.COLUMN__ID)
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var exQc = it.createObject(QcDetails::class.java, nextID)

//                        exQc?.qcID = qcData?.id
                        exQc?.enquiryID = enquiryId
                        exQc?.stageID = qcResponse?.stageId
                        exQc?.isSend = qcResponse?.isSend
                        exQc?.category = qcResponse?.category
                        exQc?.qcResponseString = responseString
//                        exQc?.questionID = qcData?.questionId
//                        exQc?.artisanID = qcData?.artisanId
//                        exQc?.answer = qcData?.answer

//                        exQc?.createdOn = qcData?.createdOn
//                        exQc?.modifiedOn = qcData?.modifiedOn
//                        exQc?.maxStageID = qcResponse?.data?.stageId
//                        exQc?.isMAxStageSend = qcResponse?.data?.stageId


                        realm.copyToRealmOrUpdate(exQc)
                    } else {
                        nextID = qcObj?._id ?: 0

                        qcObj?.enquiryID = enquiryId
                        qcObj?.stageID = qcResponse?.stageId
                        qcObj?.isSend = qcResponse?.isSend
                        qcObj?.category = qcResponse?.category
                        qcObj?.qcResponseString = responseString

//                        qcObj?.qcID = qcData?.id
//                        qcObj?.enquiryID = qcData?.enquiryId
//                        qcObj?.stageID = qcData?.stageId
//                        qcObj?.questionID = qcData?.questionId
//                        qcObj?.artisanID = qcData?.artisanId
//                        qcObj?.answer = qcData?.answer
//                        qcObj?.isSend = qcData?.isSend
//                        qcObj?.createdOn = qcData?.createdOn
//                        qcObj?.modifiedOn = qcData?.modifiedOn
//                        qcObj?.maxStageID = qcResponse?.data?.stageId
//                        qcObj?.isMAxStageSend = qcResponse?.data?.stageId

                        realm.copyToRealmOrUpdate(qcObj)
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Insert QC Response Exception: $e")
            } finally {
//                realm.close()
            }
        }

        fun insertBuyerQcResponses(qcResponse: BuyerQcResponse, enquiryId: Long) {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var qcObj: QcDetails? = null
            var responseString:String?=""
            try {
                realm.executeTransaction {

                    qcObj = realm?.where(QcDetails::class.java)
                        ?.equalTo(QcDetails?.COLUMN_ENQUIRY_ID, enquiryId)
//                        ?.and()
//                        ?.equalTo(QcDetails?.COLUMN_STAGE_ID, qcData?.stageId)
//                        ?.and()
//                        ?.equalTo(QcDetails?.COLUMN_QUESTION_ID, qcData?.questionId)
                        ?.limit(1)
                        ?.findFirst()

                    if (qcResponse != null){
                        responseString = Gson()?.toJson(qcResponse)
                    }

                    if (qcObj == null) {
                        var primId = it.where(QcDetails::class.java).max(QcDetails.COLUMN__ID)
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var exQc = it.createObject(QcDetails::class.java, nextID)

//                        exQc?.qcID = qcData?.id
                        exQc?.enquiryID = enquiryId
//                        exQc?.stageID = qcResponse?.stageId
//                        exQc?.isSend = qcResponse?.isSend
//                        exQc?.category = qcResponse?.category
                        exQc?.qcResponseString = responseString
//                        exQc?.questionID = qcData?.questionId
//                        exQc?.artisanID = qcData?.artisanId
//                        exQc?.answer = qcData?.answer

//                        exQc?.createdOn = qcData?.createdOn
//                        exQc?.modifiedOn = qcData?.modifiedOn
//                        exQc?.maxStageID = qcResponse?.data?.stageId
//                        exQc?.isMAxStageSend = qcResponse?.data?.stageId


                        realm.copyToRealmOrUpdate(exQc)
                    } else {
                        nextID = qcObj?._id ?: 0

                        qcObj?.enquiryID = enquiryId
//                        qcObj?.stageID = qcResponse?.stageId
//                        qcObj?.isSend = qcResponse?.isSend
//                        qcObj?.category = qcResponse?.category
                        qcObj?.qcResponseString = responseString

//                        qcObj?.qcID = qcData?.id
//                        qcObj?.enquiryID = qcData?.enquiryId
//                        qcObj?.stageID = qcData?.stageId
//                        qcObj?.questionID = qcData?.questionId
//                        qcObj?.artisanID = qcData?.artisanId
//                        qcObj?.answer = qcData?.answer
//                        qcObj?.isSend = qcData?.isSend
//                        qcObj?.createdOn = qcData?.createdOn
//                        qcObj?.modifiedOn = qcData?.modifiedOn
//                        qcObj?.maxStageID = qcResponse?.data?.stageId
//                        qcObj?.isMAxStageSend = qcResponse?.data?.stageId

                        realm.copyToRealmOrUpdate(qcObj)
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Insert Buyer QC Response Exception: $e")
            } finally {
//                realm.close()
            }
        }

        fun insertSavedSentQcResp(response: SaveSendQcResponse, enquiryId: Long){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var qcObj: QcDetails? = null
            var qcData = response?.data?.iterator()
            var responseString:String?=""
            try {
                realm.executeTransaction {
                    if(qcData!=null){
                        while (qcData.hasNext()){
                            var qcResponse = qcData?.next()
                            if (qcResponse != null){
                                responseString = Gson()?.toJson(qcResponse)
                            }

                            qcObj = realm?.where(QcDetails::class.java)
                                ?.equalTo(QcDetails?.COLUMN_ENQUIRY_ID, enquiryId)
                                ?.limit(1)
                                ?.findFirst()

                            if (qcObj == null) {
                                var primId = it.where(QcDetails::class.java).max(QcDetails.COLUMN__ID)
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exQc = it.createObject(QcDetails::class.java, nextID)

//                        exQc?.qcID = qcData?.id
                                exQc?.enquiryID = enquiryId
                                exQc?.stageID = qcResponse?.stageId
                                exQc?.isSend = qcResponse?.isSend
//                                exQc?.category = qcResponse?.category
                                exQc?.qcResponseString = responseString


                                realm.copyToRealmOrUpdate(exQc)
                            } else {
                                nextID = qcObj?._id ?: 0

                                qcObj?.enquiryID = enquiryId
                                qcObj?.stageID = qcResponse?.stageId
                                qcObj?.isSend = qcResponse?.isSend
//                                qcObj?.category = qcResponse?.category
                                qcObj?.qcResponseString = responseString

                                realm.copyToRealmOrUpdate(qcObj)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Insert QC Response Exception: $e")
            } finally {
//                realm.close()
            }
        }

        fun getQcById(_id: Long?): QcDetails? {
            var realm = CXRealmManager.getRealmInstance()
            var qcDetails: QcDetails? = null
            realm.executeTransaction {
                qcDetails = realm.where(QcDetails::class.java)
                    .equalTo(QcDetails.COLUMN__ID, _id)
                    .limit(1)
                    .findFirst()
            }
            return qcDetails
        }

        fun getQcMarkedForActions(actionsMarked: String): ArrayList<Long>? {
            var realm = CXRealmManager.getRealmInstance()
            var itemId = ArrayList<Long>()
            try {
                realm.executeTransaction {
                    var message1 = realm.where(QcDetails::class.java)
                        .equalTo(QcDetails.COLUMN_MARK_QC_FOR_SAVE_SEND, 1L)
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

        fun getQcResponsesByEnq(enqID: Long): QcDetails? {
            val realm = CXRealmManager.getRealmInstance()
            var obj = realm.where(QcDetails::class.java)
                .equalTo(QcDetails.COLUMN_ENQUIRY_ID, enqID)
                .limit(1)
                .findFirst()
            return obj
        }

        fun insertQcForOffline(enquiryId: Long,stageID : Long,sendSave:Long, sendQcRequest: SaveOrSendQcRequest){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var qcObj : QcDetails ?= null
            try {
                realm.executeTransaction {
                    //
                    qcObj = realm.where(QcDetails::class.java)
                        .equalTo(QcDetails.COLUMN_ENQUIRY_ID, enquiryId)
                        .limit(1)
                        .findFirst()



                    if(qcObj == null) {
                        var primId = it.where(PiDetails::class.java).max(PiDetails.COLUMN__ID)
                        if (primId == null) {
                            nextID = 1
                        } else {
                            nextID = primId.toLong() + 1
                        }
                        var qcDetails = it.createObject(QcDetails::class.java, nextID)
                        qcDetails?.enquiryID = enquiryId
                        qcDetails?.actionMarkQcForSaveSend = 1L
                        if(sendQcRequest!=null){
                            var gson = Gson()
                            var request = gson.toJson(sendQcRequest)
                            qcDetails?.qcResquestString = request
                        }
                        qcDetails?.isSend = sendSave
                        qcDetails?.stageID = stageID
                        Log.e(TAG, "Insert nextID: ${nextID}")
                        realm.copyToRealmOrUpdate(qcDetails)
                    } else{
                        qcObj?.enquiryID = enquiryId
                        qcObj?.actionMarkQcForSaveSend = 1L
                        if(sendQcRequest!=null){
                            var gson = Gson()
                            var request = gson.toJson(sendQcRequest)
                            qcObj?.qcResquestString = request
                        }
                        qcObj?.isSend = sendSave
                        qcObj?.stageID = stageID
                        Log.e(TAG, "Update enquiryId: ${enquiryId}")
                        realm.copyToRealmOrUpdate(qcObj)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "InsertUpdate Exception: ${e}")
            } finally {
//                realm.close()
            }
        }

        fun updateQcPostSendSave( enquiryId:Long){
            val realm = CXRealmManager.getRealmInstance()
            var qcObj : QcDetails ?= null
            try {
                realm.executeTransaction {
                    qcObj = realm.where(QcDetails::class.java)
                        .equalTo(QcDetails.COLUMN_ENQUIRY_ID, enquiryId)
                        .limit(1)
                        .findFirst()

                    qcObj?.actionMarkQcForSaveSend = 0L
                    qcObj?.qcResquestString = null

                    realm.copyToRealmOrUpdate(qcObj)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Update Exception: $e")
            } finally {
//                realm.close()
            }
        }


        fun getStageIdOfEnquiry(enqID: Long): QcDetails? {
            val realm = CXRealmManager.getRealmInstance()
            var obj1 = realm.where(QcDetails::class.java).equalTo(QcDetails.COLUMN_ENQUIRY_ID, enqID).findFirst()
            return obj1
        }

    }
}