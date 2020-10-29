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
            var chatItr = chatList?.iterator()

            realm.executeTransaction {
                try {
                    if (chatItr != null) {
                        while (chatItr.hasNext()) {
                            var chat = chatItr.next()
                            var chatObj = realm.where(ChatUser::class.java)
                                .equalTo(
                                    ChatUser.COLUMN_ENQUIRY_NO,
                                    chat?.enquiryNumber
                                )
                                .limit(1)
                                .findFirst()

                            if (chatObj == null) {
                                var primId = it.where(ChatUser::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exTra = it.createObject(
                                    ChatUser::class.java,
                                    nextID
                                )
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
                    Log.e("InsertChat", e.printStackTrace().toString())
                }
            }
        }

        fun insertOpenChatLog(chatDetails: ChatLogListData?,isInitiatedChat: Long?) {
            val realm = CXRealmManager.getRealmInstance()
            // var chatItr = chatDetails.data?.iterator()

            var chatList = chatDetails?.data
            //var chatList2 = chatDetails?.data?.listIterator()
            var chatItr = chatList?.iterator()

            realm.executeTransaction {
                try{
                    if (chatItr != null) {
                        while (chatItr.hasNext()) {
                            var chatItr2 = chatItr.next().chatBoxList.iterator()
                            if(chatItr2!=null){
                                while(chatItr2.hasNext()){
                                    var chatData = chatItr2.next()

                                    var chatObj = realm.where(ChatLogUserData::class.java)
                                        .equalTo(
                                            ChatLogUserData.COLUMN_ENQUIRY_ID,
                                            chatData?.enquiryId
                                        )
                                        .limit(1)
                                        .findFirst()

                                    if (chatObj == null) {
                                        var primId = it.where(ChatLogUserData::class.java).max("_id")
                                        if (primId == null) {
                                            nextID = 1
                                        } else {
                                            nextID = primId.toLong() + 1
                                        }
                                        var exTra = it.createObject(
                                            ChatLogUserData::class.java,
                                            nextID
                                        )
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

                                        exTra?.date = chatItr?.next().date


                                        realm.copyToRealmOrUpdate(exTra)
                                    } else {
                                        nextID = chatObj?._id ?: 0

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

                                        chatObj?.date = chatItr?.next().date

                                        realm.copyToRealmOrUpdate(chatObj)
                                    }

                                }
                            }
//




                        }
                    }
                }  catch (e: Exception) {
                    Log.e("InsertChat", e.printStackTrace().toString())
                }
            }
        }


        fun getUninitiatedChatList(): RealmResults<ChatUser>? {
            val realm = CXRealmManager.getRealmInstance()
            var chatObj : RealmResults<ChatUser>?= null
            realm?.executeTransaction {
                try {
                    chatObj = realm?.where(ChatUser::class.java).equalTo(ChatUser.COLUMN_IS_INITIATED_CHAT,0L).findAll()
                }catch (e: Exception){
                    Log.e("Transactions",e.printStackTrace().toString())
                }
            }
            return chatObj
        }

        fun getInitiatedChatList(): RealmResults<ChatUser>? {
            val realm = CXRealmManager.getRealmInstance()
            var chatObj : RealmResults<ChatUser>?= null
            realm?.executeTransaction {
                try {
                    chatObj = realm?.where(ChatUser::class.java).equalTo(ChatUser.COLUMN_IS_INITIATED_CHAT,1L)
                        .sort(ChatUser.COLUMN_LAST_CHAT_DATE, Sort.DESCENDING).findAll()
                }catch (e: Exception){
                    Log.e("Transactions",e.printStackTrace().toString())
                }
            }
            return chatObj
        }


        fun getChatByEnquiryId(searchString : Long): RealmResults<ChatUser>? {
            val realm = CXRealmManager.getRealmInstance()
            var chatObj : RealmResults<ChatUser> ?= null
            realm?.executeTransaction {
                try {
                    chatObj = realm?.where(ChatUser::class.java).equalTo(ChatUser.COLUMN_ENQUIRY_ID,searchString).findAll()
                }catch (e:Exception){
                    Log.e("Chats",e.printStackTrace().toString())
                }
            }
            return chatObj
        }

        fun getChatDetails(enquiryId:Long?): ChatLogUserData? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ChatLogUserData::class.java)
                .equalTo("enquiryId", enquiryId)
                .limit(1)
                .findFirst()
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