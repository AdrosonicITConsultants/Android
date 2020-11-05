package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.*
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.chat.ChatData
import com.adrosonic.craftexchange.repository.data.response.chat.ChatList
import com.adrosonic.craftexchange.repository.data.response.chat.ChatListResponse
import com.adrosonic.craftexchange.repository.data.response.chat.ChatLogListData
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
            // var chatItr = chatDetails.data?.iterator()

            var chatList = chatDetails?.data

            Log.e("Chat","Size 00000: ${chatList?.size}")
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
                            Log.e("Chat","chatObj: ${chatObj?.enquiryId}")
                            if (chatObj?.enquiryId != chat.enquiryId) {
                                var primId = it.where(ChatUser::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                Log.e("Chat","11111111111111111")
                                var exTra = it.createObject( ChatUser::class.java, nextID)
                                exTra?.enquiryNumber = chat?.enquiryNumber
                                exTra?.enquiryId = chat?.enquiryId
                                exTra?.productTypeId = chat?.productTypeId
                                exTra?.buyerCompanyName = chat?.buyerCompanyName
                                Log.e("Chat","222222222222")
                                exTra?.buyerId = chat?.buyerId
                                exTra?.enquiryGeneratedOn = chat?.enquiryGeneratedOn
                                exTra?.convertedToOrderDate = chat?.convertedToOrderDate
                                exTra?.unreadMessage = chat?.unreadMessage
                                Log.e("Chat","3333333333333333333")
                                exTra?.escalation = chat?.escalation
                                exTra?.orderStatus = chat?.orderStatus
                                exTra?.lastUpdatedOn = chat?.lastUpdatedOn
                                Log.e("Chat","44444444444444")
                                exTra?.orderStatsId = chat?.orderStatusId
                                exTra?.orderAmouunt = chat?.orderAmount
                                exTra?.changeRequestDone = chat?.changeRequestDone
                                Log.e("Chat","555555555555")
                                exTra?.lastChatDate = chat?.lastChatDate
                                exTra?.orderReceiveDate = chat?.orderReceiveDate
                                exTra?.buyerLogo = chat?.buyerLogo
                                Log.e("Chat","66666666666666")
                                exTra?.isInitiatedChat = isInitiatedChat
                                Log.e("Chat","77777777777777777")
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
                            Log.e("InsertChat","eeeeeeeeeeeeeee")
                            var firstIterator=chatItr.next()
                            var date=firstIterator?.date?:""
                            var chatItr2 = firstIterator.chatBoxList.iterator()
                            Log.e("InsertChat","aaaaaaaaaaaaaaaaa")
//                            var date=chatItr.next().date
//                            Log.e("InsertChat","000000000000")
                            if(chatItr2!=null){
                                Log.e("InsertChat","bbbbbbbbbbb")
                                while(chatItr2.hasNext()){
                                    Log.e("InsertChat","cccccccccccc")
                                    var chatData = chatItr2.next()
                                    Log.e("InsertChat","ddddddddddddddddd: ${chatData?.id}")
                                    var chatObj = realm.where(ChatLogUserData::class.java) .equalTo( ChatLogUserData.COLUMN_ID, chatData?.id).limit(1) .findFirst()
                                    Log.e("InsertChat","111111111111: ${chatObj?.id}")
                                    if (chatObj?.id != chatData?.id) {
                                        var primId = it.where(ChatLogUserData::class.java).max(ChatLogUserData.COLUMN__ID)
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        Log.e("InsertChat","2222222 if $nextID")
                                        var exTra = it.createObject( ChatLogUserData::class.java,  nextID)
                                        exTra?.id = chatData?.id
                                        exTra?.enquiryId = chatData?.enquiryId
                                        exTra?.createdOn = chatData?.createdOn
                                        Log.e("InsertChat","3333333333")
                                        exTra?.isDelete = chatData?.isDelete
                                        exTra?.mediaName = chatData?.mediaName
                                        exTra?.messageFrom = chatData?.messageFrom
                                        exTra?.messageString = chatData?.messageString
                                        Log.e("InsertChat","4444444444444")
                                        exTra?.messageTo = chatData?.messageTo
                                        exTra?.path = chatData?.path
                                        exTra?.seen = chatData?.seen
                                        exTra?.mediaType = chatData?.mediaType
                                        exTra?.isInitiatedChat = isInitiatedChat
                                        Log.e("InsertChat","date: $date")
                                        exTra?.date = date

                                        realm.copyToRealmOrUpdate(exTra)
                                    }
                                    else {
                                        Log.e("InsertChat","else 111111111111")
                                        chatObj?.enquiryId = chatData?.enquiryId
                                        chatObj?.createdOn = chatData?.createdOn
                                        chatObj?.isDelete = chatData?.isDelete
                                        Log.e("InsertChat","else 22222222222")
                                        chatObj?.mediaName = chatData?.mediaName
                                        chatObj?.messageFrom = chatData?.messageFrom
                                        chatObj?.messageString = chatData?.messageString
                                        Log.e("InsertChat","else 33333333333")
                                        chatObj?.messageTo = chatData?.messageTo
                                        chatObj?.path = chatData?.path
                                        chatObj?.seen = chatData?.seen
                                        Log.e("InsertChat","else 444444444444")
                                        chatObj?.mediaType = chatData?.mediaType
                                        chatObj?.isInitiatedChat = isInitiatedChat
                                        Log.e("InsertChat","else 55555555555: $date")
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



    }

}