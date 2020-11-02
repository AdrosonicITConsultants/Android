package com.adrosonic.craftexchange.ui.modules.artisan.qcForm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.repository.data.response.qc.ArtBuyQcResponse
import com.adrosonic.craftexchange.utils.Utility


class ViewQcRecyclerAdapter(
    var context: Context?,
    private var qcD: List<ArtBuyQcResponse>
) : RecyclerView.Adapter<ViewQcRecyclerAdapter.MyViewHolder>() {
    private var qcDetails = qcD

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var quesText: TextView = view.findViewById(R.id.exp_ques_text)
        var ansText: TextView = view.findViewById(R.id.exp_ans_text)
    }

    override fun getItemCount(): Int {
        return this.qcDetails?.size ?: 0
    }

    fun updateQcForm(newList: List<ArtBuyQcResponse>?) {
//        if (newList != null) {
           this.qcDetails = newList!!
//        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_qc_form_view_listitem, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var qcQues = qcDetails?.get(position)
        var quesNo = position?.plus(1)?.toLong()
        var quesDataItr1 = Utility.getQcQuesData()?.data?.iterator()
        quesDataItr1?.forEach { it ->
            it.forEach {
                if (it.stageId == qcQues.stageId) {
                    if (it.questionNo == quesNo) {
                        holder.quesText.text = it.question
                    }
                }
            }
        }

        holder.ansText.text = qcQues.answer ?: " - "
    }
}