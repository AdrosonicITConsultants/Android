package com.adrosonic.craftexchange.ui.modules.chat.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorLong
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.databinding.FragmentChatListBinding
import com.adrosonic.craftexchange.ui.modules.enquiry.enquiryDetails
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ChatListViewModel
import com.bumptech.glide.load.model.Model
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults

class UninitiatedChatRecyclerAdapter(var context: Context?, private var chats: RealmResults<ChatUser>) : RecyclerView.Adapter<UninitiatedChatRecyclerAdapter.MyViewHolder>() {



    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var statusIcon: ImageView = view.findViewById(R.id.chat_stage_color)
        var chatProfileImage: ImageView = view.findViewById(R.id.chat_profile_image)
        var enquiryNumber: TextView = view.findViewById(R.id.enquiry_id_text)
        var date: TextView = view.findViewById(R.id.date_text)
        var brandName: TextView = view.findViewById(R.id.brand_name)
        var chatText: TextView = view.findViewById(R.id.chat_text)
        //  var newEnqMsgChatListBtn : ImageView = view.findViewById(R.id.initiate_chat)
        var layout: ConstraintLayout = view.findViewById(R.id.item_chat_list)


    }

    var url : String ?= ""
    var date : String?=""


    interface initiateChatInterface{
        fun onInitiateChat(enquiryId: Long)
    }

    var initiateChatListner : initiateChatInterface? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UninitiatedChatRecyclerAdapter.MyViewHolder {


        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chats?.size ?: 0
    }

    fun updateUninitiatedChatList(newList: RealmResults<ChatUser>?) {
        if (newList != null) {
            this.chats = newList
        }
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: UninitiatedChatRecyclerAdapter.MyViewHolder,
        position: Int
    ) {
        var chatData = chats?.get(position)


        holder.layout.setOnClickListener {
            chatData?.enquiryId?.let { it1 -> initiateChatListner?.onInitiateChat(it1) }
        }


        var first_image = chatData?.buyerLogo.toString()

        url = Utility.getBrandLogoUrl(chatData?.buyerId, first_image)

        holder.brandName.text = chatData?.buyerCompanyName
        holder.date.text = chatData?.lastUpdatedOn?.split("T")?.get(0)
        holder.enquiryNumber.text = chatData?.enquiryNumber.toString()
        context?.let {
            ContextCompat.getColor(
                it,R.color.red_logo)
        }?.let { holder.statusIcon.setBackgroundColor(it) }
        
        context?.let { ImageSetter.setImage(it, url!!,holder?.chatProfileImage, R.drawable.artisan_logo_placeholder,  R.drawable.artisan_logo_placeholder,  R.drawable.artisan_logo_placeholder) }

    }


}