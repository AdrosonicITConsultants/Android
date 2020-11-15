package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.ArtisanProductCategory
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.chat.ChatData
import com.adrosonic.craftexchange.repository.data.response.chat.ChatList
import com.adrosonic.craftexchange.repository.data.response.chat.ChatListResponse
import com.adrosonic.craftexchange.repository.data.response.chat.ChatLogListData
import com.adrosonic.craftexchange.repository.data.response.chat.escalations.EscalationSummResponse
import com.adrosonic.craftexchange.repository.data.response.transaction.TransactionResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.Case
import io.realm.RealmResults
import io.realm.Sort
import okhttp3.ResponseBody
import java.lang.Exception

class ChatUserPredicates {

    companion object {

        private var nextID: Long? = 0

        fun insertChat(chatDetails: ChatListResponse?, isInitiatedChat: Long?) {
            val realm = CXRealmManager.getRealmInstance()
            var chatList = chatDetails?.data
            realm.executeTransaction {
                try {
                    var chatItr = chatList?.iterator()
                    if (chatItr != null) {
                        while (chatItr.hasNext()) {
                            var chat = chatItr.next()
                            var chatObj = realm.where(ChatUser::class.java)
                                .equalTo( ChatUser.COLUMN_ENQUIRY_ID, chat?.enquiryId)
                                .limit(1)
                                .findFirst()
                            if (chatObj?.enquiryId != chat.enquiryId) {
                                var primId = it.where(ChatUser::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exTra = it.createObject( ChatUser::class.java, nextID)
                                exTra?.enquiryNumber = chat?.enquiryNumber
                                exTra?.enquiryId = chat?.enquiryId
                                exTra?.productTypeId = chat?.productTypeId
                                exTra?.buyerCompanyName = chat?.buyerCompanyName
                                exTra?.buyerId = chat?.buyerId
                                exTra?.enquiryGeneratedOn = chat?.enquiryGeneratedOn
                                exTra?.convertedToOrderDate = chat?.convertedToOrderDate
                                exTra?.unreadMessage = chat?.unreadMessage
                                exTra?.escalation = chat?.escalation
                                exTra?.orderStatus = chat?.orderStatus
                                exTra?.lastUpdatedOn = chat?.lastUpdatedOn
                                exTra?.orderStatsId = chat?.orderStatusId
                                exTra?.orderAmouunt = chat?.orderAmount
                                exTra?.changeRequestDone = chat?.changeRequestDone
                                exTra?.lastChatDate = chat?.lastChatDate
                                exTra?.orderReceiveDate = chat?.orderReceiveDate
                                exTra?.buyerLogo = chat?.buyerLogo
                                exTra?.isInitiatedChat = isInitiatedChat
                                realm.copyToRealmOrUpdate(exTra)
                            } else {
                                nextID = chatObj?._id ?: 0

                                chatObj?.enquiryNumber = chat?.enquiryNumber
                                chatObj?.enquiryId = chat?.enquiryId
                                chatObj?.productTypeId = chat?.productTypeId
                                chatObj?.buyerCompanyName = chat?.buyerCompanyName

                                chatObj?.buyerId = chat?.buyerId
                                chatObj?.enquiryGeneratedOn = chat?.enquiryGeneratedOn
                                chatObj?.convertedToOrderDate = chat?.convertedToOrderDate
                                chatObj?.unreadMessage = chat?.unreadMessage

                                chatObj?.escalation = chat?.escalation
                                chatObj?.orderStatus = chat?.orderStatus
                                chatObj?.lastUpdatedOn = chat?.lastUpdatedOn

                                chatObj?.orderStatsId = chat?.orderStatusId
                                chatObj?.orderAmouunt = chat?.orderAmount
                                chatObj?.changeRequestDone = chat?.changeRequestDone

                                chatObj?.lastChatDate = chat?.lastChatDate
                                chatObj?.orderReceiveDate = chat?.orderReceiveDate
                                chatObj?.buyerLogo = chat?.buyerLogo

                                chatObj?.isInitiatedChat = isInitiatedChat
                                realm.copyToRealmOrUpdate(chatObj)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("InsertChat", e.localizedMessage)
                }
            }
        }

        fun insertOpenChatLog(chatDetails: ChatLogListData?,isInitiatedChat: Long?) {
            val realm = CXRealmManager.getRealmInstance()
            var chatList = chatDetails?.data
            realm.executeTransaction {
                try{
                    var chatItr = chatList?.iterator()
                    if (chatItr != null) {
                        while (chatItr.hasNext()) {
                            var firstIterator=chatItr.next()
                            var date=firstIterator?.date?:""
                            var chatItr2 = firstIterator.chatBoxList.iterator()
                            if(chatItr2!=null){
                                while(chatItr2.hasNext()){
                                    var chatData = chatItr2.next()
                                    var chatObj = realm.where(ChatLogUserData::class.java) .equalTo( ChatLogUserData.COLUMN_ID, chatData?.id).limit(1) .findFirst()
                                    if (chatObj?.id != chatData?.id) {
                                        var primId = it.where(ChatLogUserData::class.java).max(ChatLogUserData.COLUMN__ID)
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        var exTra = it.createObject( ChatLogUserData::class.java,  nextID)
                                        exTra?.id = chatData?.id
                                        exTra?.enquiryId = chatData?.enquiryId
                                        exTra?.createdOn = chatData?.createdOn
                                        exTra?.isDelete = chatData?.isDelete
                                        exTra?.mediaName = chatData?.mediaName
                                        exTra?.messageFrom = chatData?.messageFrom
                                        exTra?.messageString = chatData?.messageString
                                        exTra?.messageTo = chatData?.messageTo
                                        exTra?.path = chatData?.path
                                        exTra?.seen = chatData?.seen
                                        exTra?.mediaType = chatData?.mediaType
                                        exTra?.isInitiatedChat = isInitiatedChat
                                        exTra?.date = date

                                        realm.copyToRealmOrUpdate(exTra)
                                    }
                                    else {
                                        chatObj?.enquiryId = chatData?.enquiryId
                                        chatObj?.createdOn = chatData?.createdOn
                                        chatObj?.isDelete = chatData?.isDelete
                                        chatObj?.mediaName = chatData?.mediaName
                                        chatObj?.messageFrom = chatData?.messageFrom
                                        chatObj?.messageString = chatData?.messageString
                                        chatObj?.messageTo = chatData?.messageTo
                                        chatObj?.path = chatData?.path
                                        chatObj?.seen = chatData?.seen
                                        chatObj?.mediaType = chatData?.mediaType
                                        chatObj?.isInitiatedChat = isInitiatedChat
                                        chatObj?.date = date
                                        realm.copyToRealmOrUpdate(chatObj)
                                    }
                                }
                            }
                        }
                    }
                }  catch (e: Exception) {
                    Log.e("InsertChat", e.stackTrace.toString())
                }
            }
        }

        fun getInitiatedChatList(isInitiated:Long,searchString:String): RealmResults<ChatUser>? {
            val realm = CXRealmManager.getRealmInstance()
            var chatObj : RealmResults<ChatUser>?= null
            realm?.executeTransaction {
                try {
                    if(searchString.isEmpty()) {
                        chatObj = realm?.where(ChatUser::class.java)
                            .equalTo(ChatUser.COLUMN_IS_INITIATED_CHAT, isInitiated)
                            .sort(ChatUser.COLUMN_LAST_CHAT_DATE, Sort.DESCENDING).findAll()
                    }else{
                        if (searchString.matches(Regex("[0-9]+")) && searchString.length > 1){
                            chatObj = realm?.where(ChatUser::class.java)
                                .equalTo(ChatUser.COLUMN_IS_INITIATED_CHAT, isInitiated)
                                .sort(ChatUser.COLUMN_LAST_CHAT_DATE, Sort.DESCENDING).findAll()
                                .where()
                                .equalTo(ChatUser.COLUMN_ENQUIRY_ID,searchString.toInt())
                                .or()
                                .contains(ChatUser.COLUMN_BUYER_COMPANY_NAME,searchString,Case.INSENSITIVE)
                                .findAll()
                        }else  chatObj = realm?.where(ChatUser::class.java)
                            .equalTo(ChatUser.COLUMN_IS_INITIATED_CHAT, isInitiated)
                            .sort(ChatUser.COLUMN_LAST_CHAT_DATE, Sort.DESCENDING).findAll()
                            .where()
                            .contains(ChatUser.COLUMN_BUYER_COMPANY_NAME,searchString,Case.INSENSITIVE)
                            .findAll()
                    }
                }catch (e: Exception){
                    Log.e("Transactions",e.printStackTrace().toString())
                }
            }
            return chatObj
        }


        fun getChatDetailsByEnquiryId(enquiryId : Long): ChatUser? {
            val realm = CXRealmManager.getRealmInstance()
            var chatObj : ChatUser ?= null
            realm?.executeTransaction {
                try {
                    chatObj = realm?.where(ChatUser::class.java).equalTo(ChatUser.COLUMN_ENQUIRY_ID,enquiryId).findFirst()
                }catch (e:Exception){
                    Log.e("Chats",e.printStackTrace().toString())
                }
            }
            return chatObj
        }

        fun getChatDetails(enquiryId:Long?): RealmResults<ChatLogUserData>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ChatLogUserData::class.java)
                .equalTo(ChatLogUserData.COLUMN_ENQUIRY_ID, enquiryId)
                .findAll()
        }

        fun getChatHeaderDetailsFromId(enquiryId: Long?): ChatUser? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ChatUser::class.java).equalTo("enquiryId",enquiryId).limit(1).findFirst()
        }

        fun chatSearch(searchFilter : String) : RealmResults<ChatUser>? {
            var realm = CXRealmManager.getRealmInstance()
            var chats : RealmResults<ChatUser> ?= null
            realm.executeTransaction {
                chats = realm.where(ChatUser::class.java)
                    .contains("enquiryId",searchFilter, Case.INSENSITIVE)
                    .or()
                    .contains("enquiryNumber",searchFilter, Case.INSENSITIVE)
                    .or()
                    .contains("buyerCompanyName",searchFilter, Case.INSENSITIVE)
                    .findAll()

                chats?.size
            }

            return chats
        }

        fun insertEscalation(details : EscalationSummResponse) {
            val realm = CXRealmManager.getRealmInstance()
            var escObj : Escalations ?= null
            realm.executeTransaction {
                try{
                    var escDataItr = details?.data?.iterator()

                    if (escDataItr != null) {
                        while (escDataItr.hasNext()) {
                            var escData = escDataItr.next()
                            escObj = realm?.where(Escalations::class.java)
                                .equalTo(Escalations.COLUMN_ENQUIRY_ID,escData.enquiryId)
                                .and()
                                .equalTo(Escalations.COLUMN_ESCALATION_ID,escData.id)
                                .limit(1)
                                .findFirst()

                            if(escObj == null) {
                                var primId =
                                    it.where(Escalations::class.java).max(Escalations.COLUMN__ID)
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exEsc = it.createObject(Escalations::class.java, nextID)

                                exEsc.escalationId = escData?.id
                                exEsc.enquiryId = escData?.enquiryId
                                exEsc.escalationFrom = escData?.escalationFrom
                                exEsc.escalationTo = escData?.escalationTo
                                exEsc.category = escData?.category
                                exEsc.text = escData?.text
                                exEsc.isManual = escData?.isManual
                                exEsc.escalationToadmin = escData?.escalationToadmin
                                exEsc.escalationToadminDatetime = escData?.escalationToadminDatetime
                                exEsc.isResolve = escData?.isResolve
                                exEsc.resolvedOn = escData?.resolvedOn
                                exEsc.autoEscalationTypeId = escData?.autoEscalationTypeId
                                exEsc.createdOn = escData?.createdOn
                                exEsc.modifiedOn = escData?.modifiedOn

                                realm.copyToRealmOrUpdate(exEsc)

                            }else{

                                nextID = escObj?._id ?: 0

                                escObj?.escalationId = escData?.id
                                escObj?.enquiryId = escData?.enquiryId
                                escObj?.escalationFrom = escData?.escalationFrom
                                escObj?.escalationTo = escData?.escalationTo
                                escObj?.category = escData?.category
                                escObj?.text = escData?.text
                                escObj?.isManual = escData?.isManual
                                escObj?.escalationToadmin = escData?.escalationToadmin
                                escObj?.escalationToadminDatetime = escData?.escalationToadminDatetime
                                escObj?.isResolve = escData?.isResolve
                                escObj?.resolvedOn = escData?.resolvedOn
                                escObj?.autoEscalationTypeId = escData?.autoEscalationTypeId
                                escObj?.createdOn = escData?.createdOn
                                escObj?.modifiedOn = escData?.modifiedOn

                                realm.copyToRealmOrUpdate(escObj)

                            }
                        }
                    }
                }  catch (e: Exception) {
                    Log.e("InsertEscalations", e.stackTrace.toString())
                }
            }

        }

        fun getEscalationEnquiry(enquiryId : Long) : RealmResults<Escalations>? {
            val realm = CXRealmManager.getRealmInstance()
            var escObj : RealmResults<Escalations> ?= null
            realm?.executeTransaction {
                try {
                    escObj = realm?.where(Escalations::class.java)
                        .equalTo(Escalations.COLUMN_ENQUIRY_ID,enquiryId)
                        .sort(Escalations.COLUMN_MODIFIED_ON, Sort.DESCENDING)
                        .findAll()
                }catch (e:Exception){
                    Log.e("enquiryId",e.printStackTrace().toString())
                }
            }
            return escObj
        }

        fun getPendingEscalationEnquiry(enquiryId : Long) : RealmResults<Escalations>? {
            val realm = CXRealmManager.getRealmInstance()
            var escObj : RealmResults<Escalations> ?= null
            realm?.executeTransaction {
                try {
                    escObj = realm?.where(Escalations::class.java)
                        .equalTo(Escalations.COLUMN_ENQUIRY_ID,enquiryId)
                        .and()
                        .equalTo(Escalations.COLUMN_IS_RESOLVE,0L)
                        .sort(Escalations.COLUMN_MODIFIED_ON, Sort.DESCENDING)
                        .findAll()
                }catch (e:Exception){
                    Log.e("enquiryId",e.printStackTrace().toString())
                }
            }
            return escObj
        }

    }

}