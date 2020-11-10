package com.adrosonic.craftexchange.ui.modules.chat.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
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
import com.daimajia.swipe.SwipeLayout
import io.realm.RealmResults

class InitiatedChatRecyclerAdapter(var context: Context?, private var chats: RealmResults<ChatUser>,var isInitiated:Long) : RecyclerView.Adapter<InitiatedChatRecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var chat_stage_color: ImageView
        var chat_contact_pic: ImageView
        var imgEscalations: ImageView
        var txt_buyer_name: TextView
        var txt_enq_code: TextView
        var txt_date_time: TextView
        var txt_unread_count: TextView
        var ll_markasread: LinearLayout
        var swipe: SwipeLayout
        var chat_container_layout: ConstraintLayout
        init {
            chat_stage_color = view.findViewById(R.id.chat_stage_color)
            chat_contact_pic = view.findViewById(R.id.chat_contact_pic)
            imgEscalations = view.findViewById(R.id.imgEscalations)
            txt_buyer_name = view.findViewById(R.id.txt_buyer_name)
            txt_enq_code = view.findViewById(R.id.txt_enq_code)
            txt_date_time = view.findViewById(R.id.txt_date_time)
            txt_unread_count = view.findViewById(R.id.txt_unread_count)
            ll_markasread = view.findViewById(R.id.ll_markasread)
            swipe = view.findViewById(R.id.swipe)
            chat_container_layout = view.findViewById(R.id.chat_container_layout)
        }
    }

    var url : String ?= ""
    var date : String?=""

    interface ChatUpdateInterface{
        fun onChatRead(enquiryId: Long)
    }

    var onChatReadListener : ChatUpdateInterface? = null

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
        holder.chat_container_layout.setOnClickListener {
            val intent = Intent(context?.chatLogDetailsIntent())
            val bundle = Bundle()
            bundle.putLong(ConstantsDirectory.ENQUIRY_ID, chatData?.enquiryId?:0)
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }
        var image = chatData?.buyerLogo
        var first_image = chatData?.buyerLogo.toString()
        url = Utility.getBrandLogoUrl(chatData?.buyerId, first_image)

        holder.txt_buyer_name.text = chatData?.buyerCompanyName
        holder.txt_date_time.text = if(isInitiated==1L)chatData?.lastChatDate?.split("T")?.get(0) else chatData?.lastUpdatedOn?.split("T")?.get(0)
        holder.txt_enq_code.text = chatData?.enquiryNumber.toString()
        context?.let { ImageSetter.setImage(it, url!!,holder?.chat_contact_pic, R.drawable.artisan_logo_placeholder,  R.drawable.artisan_logo_placeholder,  R.drawable.artisan_logo_placeholder) }

        if(chatData?.escalation!!>0) holder.imgEscalations.visibility = View.VISIBLE
        else holder.imgEscalations.visibility = View.INVISIBLE

        if(isInitiated==1L) {
            if (chatData?.unreadMessage!! > 0) {
                holder.txt_unread_count.text = chatData?.unreadMessage.toString()
                holder.txt_unread_count.visibility = View.VISIBLE
            }
            else holder.txt_unread_count.visibility = View.INVISIBLE
        }

        if(isInitiated==0L) {
            holder!!.swipe.addDrag(SwipeLayout.DragEdge.Right, null)
            holder.txt_unread_count.visibility = View.INVISIBLE
        }
        holder?.ll_markasread?.setOnClickListener {
            if(isInitiated==1L) markAsUnread( chatData?.enquiryId?:0)
        }
    }
    fun markAsUnread(enquiryId:Long){
        notifyDataSetChanged()
        onChatReadListener?.onChatRead(enquiryId)
    }

}