package com.adrosonic.craftexchange.database.entities.realmEntities

import com.adrosonic.craftexchange.repository.data.request.qc.QuestionAnswer
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class QcDetails : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0

//    var qcID: Long ?=0
    var enquiryID: Long ?=0
    var stageID: Long ?=0
    var category: String ?=""
    var isSend: Long?=0
    var qcResponseString: String ?= ""
    var qcResquestString: String?= ""
//    var answer: String ? = ""
//    var artisanID: Long?=0

//    var createdOn: String?=""
//    var modifiedOn: String?=""
//
//    var maxStageID : Long?= 0
//    var isMAxStageSend: Long?= 0

    var actionMarkQcForSaveSend: Long? = 0

    companion object{
        const val COLUMN__ID = "_id"
//        const val COLUMN_QC_ID = "qcID"
        const val COLUMN_ENQUIRY_ID = "enquiryID"
//        const val COLUMN_QUESTION_ID = "questionID"
        const val COLUMN_STAGE_ID = "stageID"
//        const val COLUMN_MODIFIED_ON = "modifiedOn"
        const val COLUMN_QC_RESPONSE = "qcResponseString"
        const val COLUMN_QC_REQUEST = "qcResquestString"
        const val COLUMN_MARK_QC_FOR_SAVE_SEND = "actionMarkQcForSaveSend"
    }

}