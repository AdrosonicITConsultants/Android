package com.adrosonic.craftexchangemarketing.ui.modules.admin.selectArtisan

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.FilteredArtisans


class ArtisanSelectionAdapter(
    private val context: Context,
     pairList1: ArrayList<FilteredArtisans>?//dscrp, selcted-unselected,id
) : RecyclerView.Adapter<ArtisanSelectionAdapter.MyViewHolder>() {

    interface selectionListener {
        fun onArtisanItemSelected(artisanId:Long)
    }

    private var lastChecked: CheckBox? = null
    private var lastCheckedPos = 0
    private  var clickedPos=0
    var listener: selectionListener? = null
    var pairList:ArrayList<FilteredArtisans>?=null
    init {
        this.pairList=pairList1
    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgStatus: CheckBox
        val txtArtisanId: TextView
        val txtBrand: TextView
        val txtCluster: TextView

        init {
            imgStatus = view.findViewById(R.id.imgStatus)
            txtArtisanId = view.findViewById(R.id.txtArtisanId)
            txtBrand = view.findViewById(R.id.txtBrand)
            txtCluster = view.findViewById(R.id.txtCluster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_select_artisan_brand, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        val position=pos
        Log.e("Selection","position: $position")
        val item = pairList?.get(position)
        holder.txtArtisanId.text = item?.weaverID
        holder.txtBrand.text = item?.brand
        holder.txtCluster.text = item?.cluster

        if (position == 0 && pairList?.get(0)!!.selectionStatus && holder.imgStatus.isChecked()) {
            lastChecked = holder.imgStatus
            lastCheckedPos = 0
        }
        if(item!!.selectionStatus)
            {
                holder?.imgStatus?.isChecked=true
            }else  holder?.imgStatus?.isChecked=false

        holder?.imgStatus?.setOnClickListener {
            Log.e("Selection","click position: $position")
            val cb = it as CheckBox
             clickedPos =position//try{cb.tag as Int}catch (e:Exception){0}
            if (cb.isChecked) {
                if (lastChecked != null) {
                    lastChecked!!.isChecked = false
                }
                lastChecked = cb
                lastCheckedPos = clickedPos.toInt()
            } else lastChecked = null
            pairList?.get(position)?.selectionStatus=cb.isChecked
            if(holder?.imgStatus?.isChecked) listener?.onArtisanItemSelected(item?.id?:0)
            else listener?.onArtisanItemSelected(0)
        }

    }

    override fun getItemCount(): Int {
        return pairList?.size?:0
    }

    override fun getItemId(position: Int): Long {
        return pairList?.get(position)?.id?:0
    }

    fun selectDeselect(position: Int) {
//            pairList.forEach {
//              if(it.first===dscrp)   pairList.set(position,Pair(it.first, true))
//                else pairList.set(position,Pair(it.first, false))
//            }
//            notifyDataSetChanged()
//        if (selcted) pairList.set(position, Pair(dscrp, false))
//        notifyItemRangeChanged(position, pairList.size)
//        listener?.onArtisanItemSelected(dscrp.id)
    }

}