package com.adrosonic.craftexchange.ui.modules.Notification


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ItemNotificationBinding
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*


class NotificationAdapter(
    private val context: Context,
    private var notificationsData: RealmResults<Notifications>?
) : RecyclerSwipeAdapter<NotificationAdapter.MyViewHolder>() {
    interface NotificationUpdatedListener {
        fun onSelected(productId:Long,isRead:Long)
    }

    var listener: NotificationUpdatedListener? = null
    private var notificationItems=notificationsData

    fun updateNotificationlist(newFolders: RealmResults<Notifications>?){
        this.notificationItems=newFolders
        this.notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var statusImage: ImageView
        var txt_inquiry_id: TextView
        var txt_buyer_brand: TextView
        var txt_prod_details: TextView
        var txt_status: TextView
        var txt_date: TextView
        var ll_markasread:LinearLayout
        var constraint_bg:ConstraintLayout
        init {
            statusImage = view.findViewById(R.id.img_status)
            txt_inquiry_id = view.findViewById(R.id.txt_inquiry_id)
            txt_buyer_brand = view.findViewById(R.id.txt_buyer_brand)
            txt_prod_details = view.findViewById(R.id.txt_prod_details)
            txt_status = view.findViewById(R.id.txt_status)
            txt_date = view.findViewById(R.id.txt_date)
            ll_markasread = view.findViewById(R.id.ll_markasread)
            constraint_bg = view.findViewById(R.id.constraint_bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pos=position
        var notification = notificationItems?.get(position)
        holder.txt_inquiry_id.text = "Enquiry Id: ${notification?.code}"
        holder.txt_buyer_brand.text = notification?.companyName
        holder.txt_prod_details.text = notification?.productDesc
        holder.txt_status.text = notification?.type
        holder.txt_date.text =Utility.returnDisplayDate( notification?.createdOn?:"")
        Utility.setImageResource(context, holder.statusImage, getIcon(notification?.type?:""))
        holder?.ll_markasread?.setOnClickListener {
                markAsUnread(position,0,notification?.notificationId?:0)
            }
//        if(notification!!.seen!!.equals(0)){
//            holder?.constraint_bg.setBackgroundColor(Color.parseColor("#F8F0FF"))
//        } else holder?.constraint_bg.setBackgroundColor(Color.parseColor("#FFFFFF"))

    }


    override fun getItemCount(): Int {
        return notificationItems?.size?:0
    }

    fun markAsUnread(pos:Int,isUnread: Long,productId:Long){
//            notifyItemRangeChanged(pos, notificationItems?.size?:0)
            notifyDataSetChanged()
            listener?.onSelected(productId, isUnread)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout
    }

    override fun getItemId(p0: Int): Long {
        return notificationsData!!.get(p0)!!._id ?: 0
    }

    private fun getIcon(name:String): Int {
        return when (name) {
            "Enquiry Generated" -> {
                R.drawable.ic_status_recipt
            }
            "Moq Received" -> {
                R.drawable.ic_receipt//todo change
            }
            "Moq accepted" -> {
                R.drawable.ic_receipt
            }
            "Pi finalized" -> {
                R.drawable.ic_status_invoice
            }
            "Enquiry Closed" -> {
                R.drawable.ic_receipt
            }
            "Advance Payment Received" -> {
                R.drawable.ic_advance_payent_received
            }
            "Advanced Payment Accepted" -> {
                R.drawable.ic_advance_payent_received
            }
            "Advanced Payment Rejected" -> {
                R.drawable.ic_receipt
            }
            "Change Requested Initiated" -> {
                R.drawable.ic_receipt
            }
            "Change Requested Accepted" -> {
                R.drawable.ic_receipt
            }
            "Change Request Rejected" -> {
                R.drawable.ic_receipt
            }
            "Account Disable" -> {
                R.drawable.ic_receipt
            }
            "Account Enabled" -> {
                R.drawable.ic_receipt
            }
            "Tax Invoice Raised" -> {
                R.drawable.ic_receipt
            }

            "Delivery Challan Uploaded" -> {
                R.drawable.ic_receipt
            }
            "Yarn procured" -> {
                R.drawable.ic_receipt
            }
            "Yarn Dyeing" -> {
                R.drawable.ic_receipt
            }
            "Pre loom process initiated" -> {
                R.drawable.ic_receipt
            }
            "Weaving initiated" -> {
                R.drawable.ic_receipt
            }
            "Post loom process initiated" -> {
                R.drawable.ic_receipt
            }
            "Completion of Order" -> {
                R.drawable.ic_receipt
            }
            else -> {
                R.drawable.ic_receipt
            }
        }
    }
}