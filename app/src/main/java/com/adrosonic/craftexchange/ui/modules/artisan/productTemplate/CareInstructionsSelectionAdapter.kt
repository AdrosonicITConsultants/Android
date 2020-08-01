package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.utils.Utility

class CareInstructionsSelectionAdapter(
    private val context: Context,
    private val pairList: ArrayList<Triple<String, Boolean, Long>>//dscrp, selcted-unselected,id
) : RecyclerView.Adapter<CareInstructionsSelectionAdapter.MyViewHolder>() {

    interface selectionListener {
        fun onCareItemSelected(pairList: ArrayList<Triple<String, Boolean, Long>>)
    }
    var listener: CareInstructionsSelectionAdapter.selectionListener? = null
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var chkStatus: ImageView
        var txtDscrp: TextView

        init {
            chkStatus = view.findViewById(R.id.chk_status)
            txtDscrp = view.findViewById(R.id.txt_dscrp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_checked_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item = pairList.get(position)
        holder.txtDscrp.text = item.first

        if (item.second) {
            Utility.setImageResource(context, holder.chkStatus, R.drawable.ic_blue_circle_selection)
        } else {
            Utility.setImageResource(context, holder.chkStatus, R.drawable.ic_circle_un_selected)
        }
        holder.txtDscrp.setOnClickListener {
            selectDeselect(position, item.first, item.second, item.third)
        }
        holder.chkStatus.setOnClickListener {
            selectDeselect(
                position,
                item.first,
                item.second,
                item.third
            )
        }

    }

    override fun getItemCount(): Int {
        return pairList.size
    }

    fun selectDeselect(position: Int, dscrp: String, selcted: Boolean, id: Long) {
        if (selcted) pairList.set(position, Triple(dscrp, false, id))
        else pairList.set(position, Triple(dscrp, true, id))
//        notifyItemRemoved(position)
        notifyItemRangeChanged(position, pairList.size)
        listener?.onCareItemSelected(pairList)
    }

}