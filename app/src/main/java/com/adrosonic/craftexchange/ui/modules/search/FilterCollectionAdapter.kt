package com.adrosonic.craftexchange.ui.modules.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.utils.Utility

class FilterCollectionAdapter(private val context: Context,
                              private val pairList: ArrayList<Pair<String,Long>>) : RecyclerView.Adapter<FilterCollectionAdapter.MyViewHolder>() {

    interface FilterSelectionListener {
//        fun onFilterSelected(pairList: ArrayList<Triple<String, Boolean, Long>>)
fun onFilterSelected(pairList: Pair<String,Long>)
    }

    var fListener: FilterSelectionListener? = null

    var checkedPosition : Int = 0

//    var selected:Triple<String, Boolean, Long>?= null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var chkStatus: ImageView = view.findViewById(R.id.cFilter_radio_btn)
        var txtDscrp: TextView = view.findViewById(R.id.cFilter_text)
        var icon: ImageView = view.findViewById(R.id.cFilter_image)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterCollectionAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_collection_filter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pairList.size
    }

    override fun onBindViewHolder(holder: FilterCollectionAdapter.MyViewHolder, position: Int) {
        var item = pairList[position]
        var unchkItem = pairList[checkedPosition]
        holder.txtDscrp.text = item.first

        when(item.second){
            1L -> {
                //show both
                Utility.setImageResource(context, holder.icon, R.drawable.ic_showboth_collection)
            }
            2L -> {
                Utility.setImageResource(context, holder.icon, R.drawable.ic_antaran_co_design_icon)
            }
            3L -> {
                Utility.setImageResource(context, holder.icon, R.drawable.ic_artisan_self_design_icon)
            }
        }

        if (checkedPosition == -1)
        {
            Utility.setImageResource(context, holder.chkStatus, R.drawable.ic_blue_circle_selection)
        }else{
            if(checkedPosition == position){
                Utility.setImageResource(context, holder.chkStatus, R.drawable.ic_blue_circle_selection)
            }else{
                Utility.setImageResource(context, holder.chkStatus, R.drawable.ic_circle_un_selected)
            }
        }

        holder.itemView.setOnClickListener {
            selectFilter(position,Pair(item.first, item.second))
            Utility.setImageResource(context, holder.chkStatus, R.drawable.ic_blue_circle_selection)
        }
    }


    private fun selectFilter(position: Int, filterSelected : Pair<String,Long>?) {

        if (checkedPosition != position)
        {
            checkedPosition.let { notifyItemChanged(it) }
            checkedPosition = position
            notifyDataSetChanged()
        }
        filterSelected?.let { fListener?.onFilterSelected(it) }

    }
}