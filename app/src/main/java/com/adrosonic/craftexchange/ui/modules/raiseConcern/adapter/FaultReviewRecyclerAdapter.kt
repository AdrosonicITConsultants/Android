package com.adrosonic.craftexchange.ui.modules.raiseConcern.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.repository.data.request.qc.QuestionAnswer
import com.adrosonic.craftexchange.repository.data.response.faultyOrders.FaultRefData
import com.adrosonic.craftexchange.repository.data.response.qc.ArtBuyQcResponse
import com.adrosonic.craftexchange.utils.Utility

class FaultReviewRecyclerAdapter(
    var context: Context?,
    private var fD: List<FaultRefData>
) : RecyclerView.Adapter<FaultReviewRecyclerAdapter.MyViewHolder>() {
    private var faultDetails = fD

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var comment: TextView = view.findViewById(R.id.fault_comment)
        var subComment: TextView = view.findViewById(R.id.fault_subcomment)
    }

    interface UpdateFaultListInterface{
        fun updateList(faultData : FaultRefData)
    }

    var fAdapterListener: UpdateFaultListInterface?=null

    override fun getItemCount(): Int {
        return this.faultDetails?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_fault_review_recyler, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var faultQues = faultDetails?.get(position)
        faultQues?.isSelected = false

        holder.comment.text = faultQues?.subComment
        holder.subComment.text = faultQues?.comment

        holder.subComment?.setOnClickListener {
            if(faultQues?.isSelected == false){
                faultQues?.isSelected = true
                holder?.subComment?.setBackgroundDrawable(context?.let { it1 ->
                    ContextCompat.getDrawable(
                        it1,R.drawable.bg_checked_fault)
                })
                context?.let {
                    ContextCompat.getColor(
                        it,R.color.white_text)
                }?.let { holder.subComment.setTextColor(it) }

                fAdapterListener?.updateList(faultQues)
            }else{
                faultQues?.isSelected = false
                holder?.subComment?.setBackgroundDrawable(context?.let { it1 ->
                    ContextCompat.getDrawable(
                        it1,R.drawable.bg_unchecked_fault)
                })
                context?.let {
                    ContextCompat.getColor(
                        it,R.color.red_logo)
                }?.let { holder.subComment.setTextColor(it) }
                fAdapterListener?.updateList(faultQues)
            }
        }

    }
}