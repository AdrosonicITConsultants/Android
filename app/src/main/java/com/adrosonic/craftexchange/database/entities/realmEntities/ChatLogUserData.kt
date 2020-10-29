package com.adrosonic.craftexchange.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatLogUserData : RealmObject(){

    @PrimaryKey
    var _id : Long?=0
    var enquiryId : Long? = 0
    var messageFrom : Int? = 0
    var messageTo : Int? = 0
    var messageString : String? = ""
    var mediaType : Int? = 0
    var mediaName : String? = ""
    var path : String? = ""
    var seen : Int? = 0
    var isDelete : Int?= 0
    var createdOn : String? = ""
    var date : String? = ""
    var isInitiatedChat : Long? = 0

    companion object{
        const val COLUMN_ENQUIRY_ID = "enquiryId"
        const val COLUMN_DATE = "date"
    }

}