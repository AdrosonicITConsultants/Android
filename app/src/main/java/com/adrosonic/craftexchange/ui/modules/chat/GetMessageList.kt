package com.adrosonic.craftexchange.ui.modules.chat

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.ChatUserPredicates
import com.adrosonic.craftexchange.utils.UserConfig
import com.github.bassaer.chatmessageview.model.Message
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Save Chat data
 * Created by nakayama on 2017/01/13.
 */
class GetMessageList {
    /**
     * Save object data as json
     * @param context application context
     * @param key save key
     * @param object save object
     */

companion object {
        fun getMessageList(
            context: Context,
            enquiryId: Long,
            buyerName: String
        ): ArrayList<Message>? {
            var messageList = ArrayList<Message>()
            val chatList = ChatUserPredicates.getChatDetails(enquiryId)
            Log.e("GetMessageList", "chatList: ${chatList?.count()}")
            val userId = UserConfig.shared?.userId!!.toInt()
            Log.e("GetMessageList", "${userId}")
            chatList?.forEach {
                var message = Message()
                message.iconVisibility = false
                message.hideIcon(true)
                message.usernameVisibility = true
                Log.e("GetMessageList", "${it?.messageFrom}")

                if (it?.messageFrom!!.equals(userId)) {
                    Log.e("GetMessageList", "ifffff")
                    message.isRight = true
                    val yourIcon = BitmapFactory.decodeResource(
                        context.getResources(),
                        R.drawable.ic_account_circle
                    )
                    message.user = User(it?.messageFrom ?: 0, UserConfig.shared?.firstname, yourIcon)
                } else {
                    Log.e("GetMessageList", "elseeee")
                    message.isRight = false
                    val yourIcon = BitmapFactory.decodeResource(
                        context.getResources(),
                        R.drawable.ic_account_circle
                    )
                    message.user = User(it?.messageTo ?: 0, buyerName, yourIcon)
                }

//            val cal: Calendar = Calendar.getInstance()
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//            cal.setTime(sdf.parse(it.date)) // all done

                val cal: Calendar = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH)
                cal.setTime(sdf.parse(it.createdOn)) // all done
                message.sendTime=cal

                message.isDateCell = false
                message.status = it.seen ?: 0
                if (it.mediaType!!.equals(1)) {
                    message.type = Message.Type.TEXT
                    message.text = it.messageString
                }
                else {
                    message.type =Message.Type.LINK
                    message.text =  "File: "+it.mediaName
                }
                messageList.add(message)
            }
            return messageList
        }

    }
}