package com.adrosonic.craftexchangemarketing.ui.modules.buyer.enquiry.adapter


import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Moqs
import com.adrosonic.craftexchangemarketing.database.predicates.MoqsPredicates
import com.adrosonic.craftexchangemarketing.repository.data.response.moq.Datum
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import io.realm.RealmResults


class MoqAdapter(
    private val context: Context,
    private var moqsData: RealmResults<Moqs>?,
    private val moqDeliveryTimeList:ArrayList<Datum>
) : RecyclerView.Adapter<MoqAdapter.MyViewHolder>() {
    interface MoqListener {
        fun onAccepted(artisanId:Long,moqId:Long)
        fun viewArtisanProfile(id:Long)
    }

    var listener: MoqListener? = null
    private var moqItems=moqsData

//    fun updateNotificationlist(newFolders: RealmResults<Moqs>?){
//        this.moqItems=newFolders
//        this.notifyDataSetChanged()
//    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageArtisan: ImageView
        var imgExpand: ImageView
        var txtBrand: TextView
        var txtCircle: TextView
        var txtQty: TextView
        var txtPrice: TextView
        var txtEta: TextView
        var txtReceivedTime: TextView
        var textArtisanNote: TextView
        var txtViewProfile: TextView
        var txtAccept:TextView
        var detailsLayout:ConstraintLayout
        init {
            imageArtisan = view.findViewById(R.id.imageArtisan)
            imgExpand = view.findViewById(R.id.imgExpand)
            txtBrand = view.findViewById(R.id.txtBrand)
            txtCircle = view.findViewById(R.id.txtCircle)
            txtQty = view.findViewById(R.id.txtQty)
            txtPrice = view.findViewById(R.id.txtPrice)
            txtEta = view.findViewById(R.id.txtEta)
            txtReceivedTime = view.findViewById(R.id.txtReceivedTime)
            textArtisanNote = view.findViewById(R.id.textArtisanNote)
            txtViewProfile = view.findViewById(R.id.txtViewProfile)
            txtAccept = view.findViewById(R.id.txtAccept)
            detailsLayout = view.findViewById(R.id.detailsLayout)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_buyer_moq_cell, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var moq = moqItems?.get(position)
//        val minPpu=MoqsPredicates.getMinPpu(moq?.enquiryId?:0)
        val minQty=MoqsPredicates.getMinQty(moq?.enquiryId?:0)
        val minEta=MoqsPredicates.getMinEta(moq?.enquiryId?:0)
//        if(moq?.ppu?.toInt()==minPpu)holder?.txtPrice.setTypeface(Typeface.DEFAULT_BOLD)
        if(moq?.moq?.toInt()==minQty)holder?.txtQty.setTypeface(Typeface.DEFAULT_BOLD)
        if(moq?.deliveryTimeId?.toInt()==minEta)holder?.txtEta.setTypeface(Typeface.DEFAULT_BOLD)

        holder?.txtBrand.text=moq?.brand?:""
        holder?.txtCircle.text=moq?.clusterName
        holder?.txtQty.text="${moq?.moq} pcs"
        holder?.txtPrice.text="₹ ${moq?.ppu}"
        holder?.txtReceivedTime.text="Received on: ${Utility.returnDisplayDate(moq?.createdOn?:"")}"
        holder?.textArtisanNote.text="${moq?.additionalInfo}"
        if(moq?.accepted!!)holder?.txtAccept.visibility=View.GONE
        else holder?.txtAccept.visibility=View.VISIBLE

        moqDeliveryTimeList?.forEach {
            if (it.id.equals(moq?.deliveryTimeId)) {
                holder?.txtEta.text = if(it?.days.equals(0L)){"Immediate"} else "${it?.days} Days"//"${it?.days} Days"
            }
        }
        val url = Utility.getBrandLogoUrl(moq?.artisanId,moq?.logo)
        ImageSetter.setImage(context,url,holder?.imageArtisan ,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)

        holder?.txtAccept.setOnClickListener {
        acceptMoq(moq?.artisanId?:0,moq?.moqId?:0)
        }
        holder?.imgExpand.setOnClickListener {
            if(holder?.detailsLayout.visibility==View.VISIBLE)holder?.detailsLayout.visibility=View.GONE
            else holder?.detailsLayout.visibility=View.VISIBLE
        }
        holder?.txtViewProfile.setOnClickListener {
            listener?.viewArtisanProfile(moq?.artisanId?:0)
        }
    }

    override fun getItemCount(): Int {
        return moqItems?.size?:0
    }

    fun acceptMoq(artisanId:Long, moqId:Long){
//            notifyItemRangeChanged(pos, notificationItems?.size?:0)
            notifyDataSetChanged()
            listener?.onAccepted(artisanId, moqId)
    }

//    override fun getSwipeLayoutResourceId(position: Int): Int {
//        return R.id.swipeLayout
//    }

    override fun getItemId(p0: Int): Long {
        return moqsData!!.get(p0)!!._id ?: 0
    }
}