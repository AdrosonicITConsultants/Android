package com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter

import android.app.Dialog
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
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryData
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.EscalationData
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EscalationViewModel

class UpdatesRecyclerAdapter(var context: Context? ,var escalationList: ArrayList<EscalationData>, val listener :(EscalationData) -> Unit) : RecyclerView.Adapter<UpdatesRecyclerAdapter.MyViewHolder>(){


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var enquiryCode : TextView = view.findViewById(R.id.enquiryId)
        var eStatus: TextView = view.findViewById(R.id.EscalationStatus)
        var date: TextView = view.findViewById(R.id.EscalationDate)
        var daysago: TextView = view.findViewById(R.id.DaysAgo)
        var layout : ConstraintLayout = view.findViewById(R.id.escalationUpdateLayout)

    }
//    interface layoutService{
//        fun layoutclicked(ed:EscalationData)
//    }
//    var layoutlistener : layoutService?=null

    fun updateProductList( el: ArrayList<EscalationData> ){
        if (escalationList != null ) {
            this.escalationList=el
        }
        this.notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.escalation_updates_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var escalation = escalationList[position]
        holder?.enquiryCode?.text = escalation?.enquiryCode.toString()
        holder?.eStatus?.text = escalation?.stage
        holder?.date?.text = escalation?.lastUpdated?.split("T")?.get(0)
        var daysleft = Utility.getDateDiffInDays(escalation?.lastUpdated)
        holder?.daysago?.text = daysleft.toString() + "days ago"
        holder?.layout?.setOnClickListener {
            listener(escalation)
            Log.d("updatesClick", "onBindViewHolder: clicked" )
//            layoutlistener?.layoutclicked(escalation)
        }

        fun showDialog()
        {
            var dialog = Dialog(context!!)
            dialog.setContentView(R.layout.updatesdialog)

        }
    }

    override fun getItemCount(): Int {
        Log.e("EscalationVM","fn called " + escalationList?.size)

        return escalationList?.size
    }


}