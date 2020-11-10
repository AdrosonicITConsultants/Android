package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Notifications : RealmObject() {
    @PrimaryKey
    var _id :Long ?=0
    var productDesc: String?=""
    var createdOn: String?=""
    var notificationType: String?=""
//    var seen: Long?=0
//    var companyName: String?=""
    var code: String?=""
//    var customProduct: String?=""
    var notificationId: Long?=0
//    var type: String?=""
    var actionMarkRead: Long?=0
    companion object {
        const val COLUMN_TABLE = "Notifications"
        const val COLUMN__ID = "_id"
        const val COLUMN_NOTIFICATION_ID = "notificationId"
        const val COLUMN_ACTION_MARK_READ = "actionMarkRead"
        const val COLUMN_CREATED_ON = "createdOn"

    }
}

//val code: String,
//val productDesc: String,
//val createdOn: String,
//val notificationType: Long,
//val notificationId: Long