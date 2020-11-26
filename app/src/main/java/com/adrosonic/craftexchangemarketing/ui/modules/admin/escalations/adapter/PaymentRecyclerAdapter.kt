package com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.EscalationData

class PaymentRecyclerAdapter(var context: Context?, var escalationList: ArrayList<EscalationData> , val listener :(EscalationData) -> Unit) : RecyclerView.Adapter<PaymentRecyclerAdapter.MyViewHolder>(){


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var enquiryCode : TextView = view.findViewById(R.id.enquiryId)
        var escalationConcern: TextView = view.findViewById(R.id.escalationConcern)
        var date: TextView = view.findViewById(R.id.EscalationDate)
        var img : ImageView = view.findViewById(R.id.raisedby)
        var layout : ConstraintLayout = view.findViewById(R.id.paymentEscalationLayout)

//        var daysago: TextView = view.findViewById(R.id.DaysAgo)

    }
    fun updateProductList( el: ArrayList<EscalationData> ){
        if (escalationList != null ) {
            this.escalationList=el
        }
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.escalation_payment_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var escalation = escalationList[position]
        holder?.enquiryCode?.text = escalation?.enquiryCode.toString()
        holder?.escalationConcern?.text = escalation?.concern
        holder?.layout?.setOnClickListener {
            listener(escalation)
            Log.d("updatesClick", "onBindViewHolder: clicked" )
//            layoutlistener?.layoutclicked(escalation)
        }
        when (escalation?.raisedBy) {

            1L -> {
                holder?.img?.setImageResource(R.drawable.ic_for_the_light_back_ground_artisan)
            }
            2L -> {
                holder?.img?.setImageResource(R.drawable.ic_buyer1)
            }
            3L->{
                holder?.img?.setImageResource(R.drawable.ic_flag_icon)
            }

        }
        holder?.date?.text = escalation?.date?.split("T")?.get(0)

    }

    override fun getItemCount(): Int {
        Log.e("EscalationVMC","fn called " + escalationList?.size)

        return escalationList?.size
    }

}