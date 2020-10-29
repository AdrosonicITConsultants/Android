package com.adrosonic.craftexchange.database.entities.realmEntities

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatUser: RealmObject(){

    @PrimaryKey
    var _id : Long?=0
    var enquiryId : Long? = 0
    var enquiryNumber : String? = ""
    var buyerCompanyName : String? = ""
    var productTypeId : String? = ""
    var buyerId : Long? = 0
    var enquiryGeneratedOn : String? = ""
    var convertedToOrderDate : String? = ""
    var unreadMessage : Long? = 0
    var escalation : Long?= 0
    var orderStatus : String? = ""
    var lastUpdatedOn : String?= ""
    var orderStatsId : Long? = 0
    var orderAmouunt : Long? = 0
    var changeRequestDone : Long? = 0
    var lastChatDate : String? =""
    var orderReceiveDate : String?= ""
    var buyerLogo : String?=""
    var isInitiatedChat : Long? = 0


    companion object{
        const val COLUMN_ENQUIRY_ID = "enquiryId"
        const val COLUMN_ENQUIRY_NO = "enquiryNumber"
        const val COLUMN__ID = "_id"
        const val COLUMN_ORDER_RECEIVE_DATE = "orderReceiveDate"
        const val COLUMN_IS_INITIATED_CHAT = "isInitiatedChat"
        const val COLUMN_LAST_CHAT_DATE = "lastChatDate"
    }
}