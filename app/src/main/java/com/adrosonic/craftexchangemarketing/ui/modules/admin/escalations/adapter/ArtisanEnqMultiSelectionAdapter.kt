package com.adrosonic.craftexchangemarketing.ui.modules.admin.escalations.adapter

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.FilteredArtisans
import com.adrosonic.craftexchangemarketing.repository.data.response.escalationa.ArtisanData1
import com.adrosonic.craftexchangemarketing.utils.Utility

//list:List<ArtisanData>?
class ArtisanEnqMultiSelectionAdapter(
    private val context: Context,
     pairList1: ArrayList<Pair<FilteredArtisans,Boolean>>
) : RecyclerView.Adapter<ArtisanEnqMultiSelectionAdapter.MyViewHolder>() {

    interface selectionListener {
        fun onArtisanItemSelected(list:ArrayList<Pair<FilteredArtisans,Boolean>>)
    }

    var listener: selectionListener? = null
    var pairList:ArrayList<Pair<FilteredArtisans,Boolean>>?=null
    init {
        this.pairList=pairList1
    }
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgStatus: ImageView
        val imgEye: ImageView
        val txtArtisanBrand: TextView
        val txtRating: TextView
        val txtCluster: TextView

        init {
            imgStatus = view.findViewById(R.id.imgStatus)
            imgEye = view.findViewById(R.id.imgEye)
            txtArtisanBrand = view.findViewById(R.id.txtArtisanBrand)
            txtRating = view.findViewById(R.id.txtRating)
            txtCluster = view.findViewById(R.id.txtCluster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_select_redirect_artisan_brand, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        val position=pos
        Log.e("Selection","position: $position")
        val item = pairList?.get(position)
        holder.txtRating.text = item?.first?.rating?.toString()?:"NA"
        holder.txtArtisanBrand.text =item?.first?.brand?:"NA"
        holder.txtCluster.text = item?.first?.cluster?.toString()?:"NA"
        holder.imgEye.visibility=View.GONE
        if (item!!.second) {
            Utility.setImageResource(context, holder.imgStatus, R.drawable.ic_cr_checked)
        } else {
            Utility.setImageResource(context, holder.imgStatus, R.drawable.ic_cr_unchecked)
        }
        Log.e("Status","${item?.first?.status}")
//        when(item?.first?.status){
//            0L->Utility.setImageResource(context, holder.imgEye, R.drawable.ic_eye_white)
//            1L->{
//                if(item?.first?.isMailSent!!.equals(1L))  Utility.setImageResource(context, holder.imgEye, R.drawable.ic_eye_white)
//            }
//            2L->Utility.setImageResource(context, holder.imgEye, R.drawable.ic_eye_pink)
//        }
//        if(item?.first?.isMailSent!!.equals(1L) && item?.first?.artisan?.status!!.equals(2L))holder?.imgStatus?.visibility=View.INVISIBLE
//        else holder?.imgStatus?.visibility=View.VISIBLE
        holder?.imgStatus?.setOnClickListener {
            selectDeselect(position,item.first!!,item.second)
        }

    }

    override fun getItemCount(): Int {
        return pairList?.size?:0
    }

    fun selectDeselect(position: Int, dscrp: FilteredArtisans, selcted: Boolean) {
        if (selcted) pairList?.set(position, Pair(dscrp, false))
        else pairList?.set(position, Pair(dscrp, true))
//        notifyItemRemoved(position)
        notifyItemRangeChanged(position, pairList?.size?:0)
        listener?.onArtisanItemSelected(pairList!!)
    }

}