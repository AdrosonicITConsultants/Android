package com.adrosonic.craftexchange.ui.modules.chat.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ChatListViewModel
import io.realm.RealmResults

class InitiatedChatRecyclerAdapter(var context: Context?, private var chats: RealmResults<ChatUser>) : RecyclerView.Adapter<InitiatedChatRecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var statusIcon: ImageView = view.findViewById(R.id.chat_stage_color)
        var chatProfileImage: ImageView = view.findViewById(R.id.chat_profile_image)
        var enquiryNumber: TextView = view.findViewById(R.id.enquiry_id_text)
        var date: TextView = view.findViewById(R.id.date_text)
        var brandName: TextView = view.findViewById(R.id.brand_name)
        var chatText: TextView = view.findViewById(R.id.chat_text)
      //var newEnqMsgChatListBtn : ImageView = view.findViewById(R.id.initiate_chat)
        var layout: ConstraintLayout = view.findViewById(R.id.item_chat_list)

    }



    var url : String ?= ""
    var date : String?=""
    //var chatListener: ChatListViewModel.NewEnqMessageChatListGenListener?=null

    interface openChatLogInterface{
        fun openChatLog(enquiryId: Long)
    }

    var openChatLogListner : openChatLogInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chats?.size ?: 0
    }

    fun updateInitiatedChatList(newList: RealmResults<ChatUser>?) {
        if (newList != null) {
            this.chats = newList
        }
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var chatData = chats?.get(position)

        holder.layout.setOnClickListener {
            val intent = Intent(context?.chatLogDetailsIntent())
            val bundle = Bundle()
            bundle.putString(ConstantsDirectory.ENQUIRY_ID, chatData?.enquiryId?.toString())
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }

        var image = chatData?.buyerLogo
       // val imgArrSplit = image?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        var first_image = chatData?.buyerLogo.toString()

        url = Utility.getBrandLogoUrl(chatData?.buyerId, first_image)

        holder.brandName.text = chatData?.buyerCompanyName
        holder.date.text = chatData?.lastChatDate?.split("T")?.get(0)
        holder.enquiryNumber.text = chatData?.enquiryNumber.toString()
        context?.let { ImageSetter.setImage(it, url!!,holder?.chatProfileImage, R.drawable.artisan_logo_placeholder,  R.drawable.artisan_logo_placeholder,  R.drawable.artisan_logo_placeholder) }


    }

//    fun generateNewEnquiryMessageChatList(enquiryId : Long){
//        chatListener?.generateNewEnquiryMessageChatList(enquiryId)
//    }
}