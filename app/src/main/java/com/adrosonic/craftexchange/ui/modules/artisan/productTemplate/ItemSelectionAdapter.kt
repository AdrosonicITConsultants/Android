package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.parseColor
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.utils.ImageSetter
import java.io.File

class ItemSelectionAdapter(
    private val context: Context,
    private val pairList: ArrayList<Triple<String,Boolean,Long>>//dscrp, selcted-unselected,id
) : RecyclerView.Adapter<ItemSelectionAdapter.MyViewHolder>() {

    interface selectionListener {
        fun onItemSelected(pairList: ArrayList<Triple<String,Boolean,Long>>)
    }

    var listener: ItemSelectionAdapter.selectionListener? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var chkStatus: ImageView
        var txtDscrp: TextView

        init {
            chkStatus = view.findViewById(R.id.chk_status)
            txtDscrp = view.findViewById(R.id.txt_dscrp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context) .inflate(R.layout.item_checked_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item = pairList.get(position)
        holder.txtDscrp.text=item.first

        if(item.second){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.chkStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_blue_circle_selection, context.getTheme()));
            } else {
                holder.chkStatus.setImageDrawable(context.getDrawable(R.drawable.ic_blue_circle_selection));
            }
        }
        else{
//            holder.chkStatus.setBackgroundResource(R.drawable.ic_circle_un_selected)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.chkStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_circle_un_selected, context.getTheme()));
            } else {
                holder.chkStatus.setImageDrawable(context.getDrawable(R.drawable.ic_circle_un_selected));
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if(item.second)holder.chkStatus.setImageResource(context.resources.getDrawable(R.drawable.ic_blue_circle_selection,context.getTheme()))
//            else holder.chkStatus.setImageResource(context.resources.getDrawable(R.drawable.ic_circle_un_selected,context.getTheme()))
//        } else {
//           if(item.second) holder.chkStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_blue_circle_selection));
//            else holder.chkStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_circle_un_selected));
//        }
        holder.txtDscrp.setOnClickListener {
            selectDeselect(position,item.first,item.second,item.third)
        }

    }

    override fun getItemCount(): Int {
        return pairList.size
    }

    fun selectDeselect(position: Int,dscrp:String,selcted:Boolean,id:Long) {
      if(selcted)  pairList.set(position, Triple(dscrp,false,id))
      else  pairList.set(position, Triple(dscrp,true,id))
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, pairList.size)
        listener?.onItemSelected(pairList)
    }

}