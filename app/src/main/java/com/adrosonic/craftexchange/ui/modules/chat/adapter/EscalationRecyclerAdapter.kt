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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ChatUser
import com.adrosonic.craftexchange.database.entities.realmEntities.Escalations
import com.adrosonic.craftexchange.ui.modules.buyer.wishList.WishlistAdapter
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.daimajia.swipe.SwipeLayout
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults

class EscalationRecyclerAdapter(var context: Context?, private var escalation: RealmResults<Escalations>?) : RecyclerView.Adapter<EscalationRecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //sentLayout
        var sentLayout: ConstraintLayout = view.findViewById(R.id.item_sent_escalation)
        var sentdesc: TextView = view.findViewById(R.id.txt_sen_description_esc)
        var sentStatus: TextView = view.findViewById(R.id.txt_sen_status_esc)
        var sentDate: TextView = view.findViewById(R.id.txt_sen_date)


        //receiveLayout
        var receiveLayout: ConstraintLayout = view.findViewById(R.id.item_receive_escalation)
        var recDesc: TextView = view.findViewById(R.id.txt_rec_description_esc)
        var recStatus: TextView = view.findViewById(R.id.txt_rec_status_esc)
        var recDate: TextView = view.findViewById(R.id.txt_rec_date)
    }

    interface EscalationMarkResolvedListener {
        fun onMarkResolved(escalationId: Long?)
    }

    var escListener: EscalationMarkResolvedListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_escalation, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return escalation?.size ?: 0
    }

    fun updateEscalationList(newList: RealmResults<Escalations>?) {
        if (newList != null) {
            this.escalation = newList
        }
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var escalationData = escalation?.get(position)
        var userId = Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong()

        if(userId == escalationData?.escalationFrom){
            //Sent item
            holder.sentLayout.visibility = View.VISIBLE
            holder.receiveLayout.visibility = View.GONE

            holder.sentdesc.text = escalationData?.text
            var date = escalationData?.createdOn?.split("T")?.get(0)
            holder.sentDate.text = date

            if(escalationData?.escalationToadmin == 0L){
                when(escalationData?.isResolve){
                    0L -> {
                        holder?.sentStatus?.text = context?.getString(R.string.mark_resolved)
                        context?.let {
                            ContextCompat.getColor(
                                it, R.color.mark_resolve)
                        }?.let { holder.sentStatus.setTextColor(it) }

                        val img = context?.resources?.getDrawable(R.drawable.ic_mark_resolve)
                        holder?.sentStatus?.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                    }

                    1L -> {
                        holder?.sentStatus?.text = context?.getString(R.string.resolved)
                        context?.let {
                            ContextCompat.getColor(
                                it, R.color.resolved)
                        }?.let { holder.sentStatus.setTextColor(it) }

                        val img = context?.resources?.getDrawable(R.drawable.ic_resolved)
                        holder?.sentStatus?.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                    }
                }
            }else{
                holder?.sentStatus?.text = context?.getString(R.string.auto_escalated)
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.red_logo)
                }?.let { holder.sentStatus.setTextColor(it) }

                val img = context?.resources?.getDrawable(R.drawable.ic_red_exclamation)
                holder?.sentStatus?.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
            }

        }else{
            //Received item
            holder.sentLayout.visibility = View.GONE
            holder.receiveLayout.visibility = View.VISIBLE

            holder.recDesc.text = escalationData?.text

            var date = escalationData?.createdOn?.split("T")?.get(0)
            holder.recDate.text = date

            when(escalationData?.isResolve){
                0L -> {
                    holder?.recStatus?.text = context?.getString(R.string.awaiting_response)
                    context?.let {
                        ContextCompat.getColor(
                            it, R.color.red_logo)
                    }?.let { holder.recStatus.setTextColor(it) }

                    val img = context?.resources?.getDrawable(R.drawable.ic_red_exclamation)
                    holder?.recStatus?.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)
                }

                1L -> {
                    holder?.recStatus?.text = context?.getString(R.string.resolved)
                    context?.let {
                        ContextCompat.getColor(
                            it, R.color.resolved)
                    }?.let { holder.recStatus.setTextColor(it) }

                    val img = context?.resources?.getDrawable(R.drawable.ic_green_exclamation)
                    holder?.recStatus?.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)
                }
            }
        }

        holder?.sentStatus?.setOnClickListener {
            if(escalationData?.isResolve == 0L && escalationData?.escalationToadmin == 0L){
                markResolvedEscalaltion(escalationData?.escalationId)
            }
        }
    }

    fun markResolvedEscalaltion(escalationId: Long?){
        escListener?.onMarkResolved(escalationId)
    }

}