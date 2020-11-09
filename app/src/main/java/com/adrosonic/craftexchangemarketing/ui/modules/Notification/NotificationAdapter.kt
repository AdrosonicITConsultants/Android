package com.adrosonic.craftexchangemarketing.ui.modules.Notification


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
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.databinding.ItemNotificationBinding
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import java.text.SimpleDateFormat
import java.util.*


class NotificationAdapter(
    private val context: Context,
    private var notificationsData: RealmResults<Notifications>?
) : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
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
        var txtNotiType: TextView
        var txtNotiCode: TextView
        var txtNotiDate: TextView
        init {
            txtNotiType = view.findViewById(R.id.txtNotiType)
            txtNotiCode = view.findViewById(R.id.txtNotiCode)
            txtNotiDate = view.findViewById(R.id.txtNotiDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pos=position
        var notification = notificationItems?.get(position)
        holder.txtNotiType.text =notification?.notificationType?:""
        holder.txtNotiCode.text = notification?.code


        val cal: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH)
        cal.setTime(sdf.parse(notification?.createdOn))
        cal.time
        //2020-11-06T00:30:01.000+0000
        holder.txtNotiDate.text ="${cal.get(Calendar.DAY_OF_MONTH)}-"+ cal.get(Calendar.MONTH)+"-"+ cal.get(Calendar.YEAR)+"\n"+cal.get(Calendar.HOUR_OF_DAY)+":"+ cal.get(Calendar.MINUTE)
    }


    override fun getItemCount(): Int {
        return notificationItems?.size?:0
    }

    fun markAsUnread(pos:Int,isUnread: Long,productId:Long){
//            notifyItemRangeChanged(pos, notificationItems?.size?:0)
            notifyDataSetChanged()
            listener?.onSelected(productId, isUnread)
    }

//    override fun getSwipeLayoutResourceId(position: Int): Int {
//        return R.id.swipeLayout
//    }

    override fun getItemId(p0: Int): Long {
        return notificationsData!!.get(p0)!!._id ?: 0
    }

//    private fun getIcon(name:String): Int {
//        return when (name) {
//            "Enquiry Generated", "Enquiry Closed" -> {
//                R.drawable.ic_status_recipt
//            }
//            "Moq Received","Moq accepted"  -> {
//                R.drawable.ic_moq_received
//            }
//            "Pi finalized", "Tax Invoice Raised","Delivery Challan Uploaded","Order Received" -> {
//                R.drawable.ic_status_invoice
//            }
//            "Advance Payment Received","Advanced Payment Accepted","Advanced Payment Rejected" -> {
//                R.drawable.ic_advance_payent_received
//            }
//            "Change Requested Initiated","Change Requested Accepted","Change Request Rejected"  -> {
//                R.drawable.ic_cr_accepted
//            }
//            "Account Disable" -> {
//                R.drawable.ic_receipt
//            }
//            "Account Enabled" -> {
//                R.drawable.ic_receipt
//            }
//            "Yarn procured" -> {
//                R.drawable.ic_receipt
//            }
//            "Yarn Dyeing" -> {
//                R.drawable.ic_receipt
//            }
//            "Pre loom process initiated" -> {
//                R.drawable.ic_receipt
//            }
//            "Weaving initiated" -> {
//                R.drawable.ic_receipt
//            }
//            "Post loom process initiated" -> {
//                R.drawable.ic_receipt
//            }
//            "Completion of Order" -> {
//                R.drawable.ic_receipt
//            }
//            else -> {
//                R.drawable.ic_receipt
//            }
//        }
//    }
}