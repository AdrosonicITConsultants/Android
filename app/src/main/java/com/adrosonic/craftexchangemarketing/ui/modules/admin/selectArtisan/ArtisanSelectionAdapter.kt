package com.adrosonic.craftexchangemarketing.ui.modules.admin.selectArtisan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.AdminProductCatalogue
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.FilteredArtisans
import com.adrosonic.craftexchangemarketing.utils.Utility
import io.realm.RealmResults

class ArtisanSelectionAdapter(
    private val context: Context,
    private val pairList1: ArrayList<Pair<FilteredArtisans, Boolean>>//dscrp, selcted-unselected,id
) : RecyclerView.Adapter<ArtisanSelectionAdapter.MyViewHolder>() {

    interface selectionListener {
        fun onArtisanItemSelected(pairList: ArrayList<Pair<FilteredArtisans, Boolean>>)
    }
    var listener: selectionListener? = null
    var pairList=pairList1
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgStatus: ImageView
        var txtArtisanId: TextView
        var txtBrand: TextView
        var txtCluster: TextView

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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item = pairList.get(position)
        holder.txtArtisanId.text = item.first?.weaverID
        holder.txtBrand.text = item.first?.brand
        holder.txtCluster.text = item.first?.cluster

//        if(artisanIdSelected.equals(item.first?.id))Utility.setImageResource(context, holder.imgStatus, R.drawable.ic_cr_checked)
//        else Utility.setImageResource(context, holder.imgStatus, R.drawable.ic_cr_unchecked)
        if (item.second) {
            Utility.setImageResource(context, holder.imgStatus, R.drawable.ic_cr_checked)
        } else {
            Utility.setImageResource(context, holder.imgStatus, R.drawable.ic_cr_unchecked)
        }
        holder.itemView.setOnClickListener {
            selectDeselect(position, item.first, item.second)
        }
    }

    override fun getItemCount(): Int {
        return pairList.size
    }


    fun updateProducts(newList: ArrayList<Pair<FilteredArtisans,Boolean>>){
        this.pairList=newList
        this.notifyDataSetChanged()
    }
    fun selectDeselect(position: Int, dscrp: FilteredArtisans, selcted: Boolean) {
//            pairList.forEach {
//              if(it.first===dscrp)   pairList.set(position,Pair(it.first, true))
//                else pairList.set(position,Pair(it.first, false))
//            }
//            notifyDataSetChanged()
        if (selcted) pairList.set(position, Pair(dscrp, false))
        notifyItemRangeChanged(position, pairList.size)
        listener?.onArtisanItemSelected(pairList)
    }

}