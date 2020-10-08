package com.adrosonic.craftexchangemarketing.ui.modules.enquiry.adapter

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
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CompletedEnquiries
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchangemarketing.ui.modules.enquiry.enquiryDetails
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults

class CompletedEnqRecyclerAdapter(var context: Context?, private var enquiries: RealmResults<CompletedEnquiries>) : RecyclerView.Adapter<CompletedEnqRecyclerAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var productImage: ImageView = view.findViewById(R.id.product_image)
        var enquiryCode : TextView = view.findViewById(R.id.enquiry_id_text)
        var productName: TextView = view.findViewById(R.id.product_name)
        var brandName: TextView = view.findViewById(R.id.brand_name)
        var productStatus: TextView = view.findViewById(R.id.product_status_text)
//        var productAmount : TextView = view.findViewById(R.id.product_amount)
        var dateText: TextView = view.findViewById(R.id.date_text)
        var enquiryStage : TextView = view.findViewById(R.id.enquiry_status_text)
//        var enquiryStageDot: ImageView = view.findViewById(R.id.enquiry_status_dot)
        var enquiryColor : ImageView = view.findViewById(R.id.enq_stage_color)
        var layout : ConstraintLayout = view.findViewById(R.id.enquiry_cont_comp_layout)
    }

    var date : String?=""
    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var url : String ?= ""

    override fun getItemCount(): Int {
        return enquiries?.size?:0
    }

    fun updateProductList(newList: RealmResults<CompletedEnquiries>?){
        if (newList != null) {
            this.enquiries=newList
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_closed_enquiry_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var enquiry = enquiries?.get(position)

        holder.layout.setOnClickListener {
            val intent = Intent(context?.enquiryDetails())
            var bundle = Bundle()
            Prefs.putString(ConstantsDirectory.ENQUIRY_ID, enquiry?.enquiryID?.toString()) //TODO change later
            bundle.putString(ConstantsDirectory.ENQUIRY_ID, enquiry?.enquiryID?.toString())
//            bundle.putString(ConstantsDirectory.ENQUIRY_STATUS_FLAG, enquiry?.enquiryStatusID?.toString())
            bundle.putString(ConstantsDirectory.ENQUIRY_STATUS_FLAG, "1")
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }


        var image = enquiry?.productImages
        val imgArrSplit = image?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        var first_image = imgArrSplit?.get(0)

        if(enquiry?.productType == "Custom Product"){
            holder?.brandName?.text = "Custom Design by you"
            url = Utility.getCustomProductImagesUrl(enquiry?.productID, first_image)
        }else{
            holder?.brandName?.text = enquiry?.ProductBrandName
            url = Utility.getProductsImagesUrl(enquiry?.productID, first_image)
        }
        context?.let { ImageSetter.setImage(it, url!!,holder?.productImage) }


        holder?.enquiryCode?.text = enquiry?.enquiryCode

        if(enquiry?.productName != ""){
            holder?.productName?.text = enquiry?.productName
        }else{
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if(it.first == enquiry?.weftYarnID){
                    weft = it.second
                }
                if(it.first == enquiry?.warpYarnID){
                    warp = it.second
                }
                if(it.first == enquiry?.extraWeftYarnID){
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if(it.first == enquiry?.productCategoryID){
                    prodCategory = it.second
                }
            }
            var fp = SpannableString("${prodCategory} / ")
            var sp = "${warp} X ${weft} X ${extraweft}"
            fp.setSpan(context?.let { ContextCompat.getColor(it, R.color.black_text) }?.let {
                ForegroundColorSpan(
                    it
                )
            }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder?.productName?.text = fp
            holder?.productName?.append(sp)
        }

        var status : String ?= ""
        when(enquiry?.productStatusID){
            2L -> {
                status = context?.getString(R.string.in_stock)
                holder.productStatus.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green)
                }?.let { holder.productStatus.setTextColor(it) }
            }
            1L -> {
                status = context?.getString(R.string.made_to_order)
                holder.productStatus.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta)
                }?.let { holder.productStatus.setTextColor(it) }
            }
            else -> {
                status = context?.getString(R.string.requested_custom_design)
                holder.productStatus.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta)
                }?.let { holder.productStatus.setTextColor(it) }
            }
        }

        if(enquiry?.lastUpdated != ""){
            date = enquiry?.lastUpdated?.split("T")?.get(0)
            holder.dateText.text = "Updated on : $date"
        }else{
            date = enquiry?.startedOn?.split("T")?.get(0)
            holder.dateText.text = "Created on : $date"
        }

        if(enquiry?.enquiryStageID == 10L){
            context?.let {
                ContextCompat.getColor(
                    it, R.color.black_text)
            }?.let { holder.enquiryColor.setBackgroundColor(it) }

            context?.let {
                ContextCompat.getColor(
                    it, R.color.black_text)
            }?.let { holder.enquiryStage.setTextColor(it) }
            holder?.enquiryStage.text = "Order Completed"
        }else{
            context?.let {
                ContextCompat.getColor(
                        it, R.color.red_logo)
                }?.let { holder.enquiryColor.setBackgroundColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it, R.color.red_logo)
                }?.let { holder.enquiryStage.setTextColor(it) }
            holder?.enquiryStage.text = "Enquiry Closed"
        }

    }

}