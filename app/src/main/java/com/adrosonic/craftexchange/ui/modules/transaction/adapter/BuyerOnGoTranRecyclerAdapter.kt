package com.adrosonic.craftexchange.ui.modules.transaction.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Transactions
import com.adrosonic.craftexchange.ui.modules.enquiry.enquiryDetails
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults


class BuyerOnGoTranRecyclerAdapter(var context: Context?, private var transactions: RealmResults<Transactions>) : RecyclerView.Adapter<BuyerOnGoTranRecyclerAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var statusIcon: ImageView = view.findViewById(R.id.transaction_icon)
        var enquiryCode : TextView = view.findViewById(R.id.enquiry_code)
        var date : TextView = view.findViewById(R.id.transaction_date)
        var paymentType : TextView = view.findViewById(R.id.payment_type)
        var paymentAction : TextView = view.findViewById(R.id.payment_status)
        var dropDown : ImageView = view.findViewById(R.id.dropdown_icon)
        var amount : TextView = view.findViewById(R.id.transaction_amount)
        var time : TextView = view.findViewById(R.id.transaction_time)
        var btn_view_invoice : TextView = view.findViewById(R.id.btn_view_invoice)
        var btn_enquiry : ImageView = view.findViewById(R.id.btn_enquiry)

        var bottomlayout : LinearLayout = view.findViewById(R.id.transac_bottom_part)
    }

    var date : String?=""
    var time : String ?= ""

    override fun getItemCount(): Int {
        return transactions?.size?:0
    }

    fun updateTransactionList(newList: RealmResults<Transactions>?){
        if (newList != null) {
            this.transactions=newList
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var transaction = transactions?.get(position)

        if(transaction?.enquiryCode != null){
            holder?.enquiryCode?.text = transaction?.enquiryCode
        }else{
            holder?.enquiryCode?.text = transaction?.orderCode
        }


        if(transaction?.transactionOn != ""){
            date = transaction?.transactionOn?.split("T")?.get(0)
            holder.date.text = date
            time = transaction?.transactionOn?.split("T")?.get(1)
            time = time?.split(".")?.get(0)
            holder?.time?.text = time
        }

        holder?.amount?.text = "₹ ${transaction?.paidAmount}"

        val slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)

        holder?.dropDown?.setOnClickListener {
            if(holder?.bottomlayout?.visibility == View.GONE){
                holder?.bottomlayout?.visibility = View.VISIBLE
                holder?.bottomlayout?.animation = slideDown
                holder?.dropDown?.setImageResource(R.drawable.ic_key_up_grey)
            }else{
                holder?.bottomlayout?.visibility = View.GONE
                holder?.bottomlayout?.animation = slideUp
                holder?.dropDown?.setImageResource(R.drawable.ic_key_down_grey)
            }
        }

    }

}