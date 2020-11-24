package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.cr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ChangeRequests
import com.adrosonic.craftexchangemarketing.repository.data.response.changeReequest.CrOption

class CrAcceptRejectAdapter(
    private val context: Context,
    private val pairList: ArrayList<Pair<ChangeRequests, Boolean>>,//dscrp, selcted-unselected,id
    private val stageList :ArrayList<CrOption>,
    private val isEnabled :Boolean
) : RecyclerView.Adapter<CrAcceptRejectAdapter.MyViewHolder>() {

    interface selectionListener {
        fun onCrItemSelected(pairList: ArrayList<Pair<ChangeRequests, Boolean>>)
    }
    var listener: selectionListener? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgStatus: ImageView
        var txtCrValue: TextView
        var txtCrTitle: TextView
        init {
            imgStatus = view.findViewById(R.id.imgStatus)
            txtCrValue = view.findViewById(R.id.txtCrValue)
            txtCrTitle = view.findViewById(R.id.txtCrTitle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cr_check_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item = pairList.get(position)
        //todo foreach here
        stageList.forEach {
            if (item.first.requestItemsId!!.equals(it.id)) {
                holder.txtCrTitle.text = it.item
            }
        }
        holder.txtCrValue.text= item.first.requestText
        if (item.second) {
            Utility.setImageResource(context, holder.imgStatus, R.drawable.ic_cr_checked)
        } else {
            Utility.setImageResource(context, holder.imgStatus, R.drawable.ic_cr_unchecked)
        }

        if(isEnabled){
            holder.itemView?.setOnClickListener {
                selectDeselect(position, item.first, item.second)
            }
        }
    }

    override fun getItemCount(): Int {
        return pairList.size
    }

    fun selectDeselect(position: Int,crObj:ChangeRequests , selcted: Boolean) {
        if (selcted) pairList.set(position, Pair(crObj, false))
        else pairList.set(position, Pair(crObj, true))
        notifyItemRangeChanged(position, pairList.size)
        listener?.onCrItemSelected(pairList)
    }

}