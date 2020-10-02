package com.adrosonic.craftexchange.ui.modules.transaction.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Transactions
import com.adrosonic.craftexchange.repository.data.request.pi.SendPiRequest
import com.adrosonic.craftexchange.repository.data.response.transaction.TranStatData
import com.adrosonic.craftexchange.ui.modules.artisan.enquiry.pi.raisePiContext
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.ui.modules.transaction.viewDocument
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
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
        var btnDoc : TextView = view.findViewById(R.id.btn_view_doc)
        var btn_enquiry : ImageView = view.findViewById(R.id.btn_enquiry)

        var bottomlayout : LinearLayout = view.findViewById(R.id.transac_bottom_part)
    }

    var date : String?=""
    var time : String ?= ""
    var accTxt : String?= ""
    var upcTxt : String?= ""

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
        var tranStatusData = Utility?.getTransactionStatusData()

        tranStatusData?.forEach {
            if(transaction?.accomplishedStatus == it.transactionId){
                accTxt = it?.buyerText
            }
            if(transaction?.upcomingStatus == it.transactionId){
                upcTxt = it?.buyerText
            }
        }

            //Status Icon
        when(transaction?.accomplishedStatus){
            1L -> {
                holder?.statusIcon?.setImageResource(R.drawable.ic_pfi_received)
            }

            12L -> {
                holder?.statusIcon?.setImageResource(R.drawable.ic_txi_received)
            }

            16L,18L -> {
                holder?.statusIcon?.setImageResource(R.drawable.ic_final_payment)
            }
            else -> {
                holder?.statusIcon?.setImageResource(R.drawable.ic_home)
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder?.paymentType.text = Html.fromHtml(accTxt, Html.FROM_HTML_MODE_COMPACT)
            holder?.paymentAction.text = Html.fromHtml(upcTxt, Html.FROM_HTML_MODE_COMPACT)
        } else {
            holder?.paymentType.text = Html.fromHtml(accTxt)
            holder?.paymentAction.text = Html.fromHtml(upcTxt)
        }

        if(transaction?.enquiryCode != null){
            holder?.enquiryCode?.text = transaction?.enquiryCode
        }else{
            holder?.enquiryCode?.text = transaction?.orderCode
        }

        holder?.btn_enquiry?.setOnClickListener {
            context?.let { it1 -> Utility?.messageDialog(it1,"Fix in Progress (direct to enquiry screen)") }
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

        when(transaction?.accomplishedStatus){
            1L,2L,3L,4L,5L,12L,13L -> {
                holder.btnDoc?.text = context?.getString(R.string.view_invoice)
                context?.let { ContextCompat.getColor(it, R.color.view_invoice) }?.let { holder.btnDoc.setTextColor(it) }
                holder?.btnDoc?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view_invoice, 0, 0, 0)
            }
            6L,7L,8L,9L,10L,11L,14L,15L,16L,17L,18L,19L,20L,21L,22L,23L -> {
                holder.btnDoc?.text = context?.getString(R.string.view_receipt)
                context?.let { ContextCompat.getColor(it, R.color.view_receipt) }?.let { holder.btnDoc.setTextColor(it) }
                holder?.btnDoc?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view_receipt, 0, 0, 0)
            }
        }

        holder?.btnDoc?.setOnClickListener {
            when(transaction?.accomplishedStatus){
                //View PI
                1L -> {
                    val intent = Intent(transaction?.enquiryID?.let { it1 -> context?.raisePiContext(it1, true, SendPiRequest()) })
                    context?.startActivity(intent)
                }
                //View Advance Payment
                8L,10L -> {
                    val intent = Intent(transaction?.enquiryID?.let { it1 ->
                        context?.viewDocument(
                            it1
                        )
                    })
                    context?.startActivity(intent)
                }
            }
        }

    }

}