package com.adrosonic.craftexchange.ui.modules.order.adapter

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
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.ui.modules.enquiry.enquiryDetails
import com.adrosonic.craftexchange.ui.modules.order.orderDetails
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults

class ArtisanOngoingOrderListAdapter(var context: Context?, private var orders: RealmResults<Orders>) : RecyclerView.Adapter<ArtisanOngoingOrderListAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var productImage: ImageView = view.findViewById(R.id.product_image)
        var enquiryCode : TextView = view.findViewById(R.id.enquiry_id_text)
        var productName: TextView = view.findViewById(R.id.product_name)
        var brandName: TextView = view.findViewById(R.id.brand_name)
        var productStatus: TextView = view.findViewById(R.id.product_status_text)
        var enquiryStageStatus : TextView = view.findViewById(R.id.enquiry_stage_txt)
        var dateText: TextView = view.findViewById(R.id.date_text)
        var enquiryColor : ImageView = view.findViewById(R.id.enq_stage_color)
        var reqDoc : TextView = view.findViewById(R.id.req_doc_text)
        var layout : ConstraintLayout = view.findViewById(R.id.enquiry_container_layout)
    }

    var date : String?=""
    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var url : String ?= ""

    override fun getItemCount(): Int {
        return orders?.size?:0
    }

    fun updateProductList(newList: RealmResults<Orders>?){
        if (newList != null) {
            this.orders=newList
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_artisan_enquiry_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var enquiry = orders?.get(position)

        holder.layout.setOnClickListener {
                val intent = Intent(context?.orderDetails())
                var bundle = Bundle()
                Prefs.putString(ConstantsDirectory.ENQUIRY_ID, enquiry?.enquiryId?.toString()) //TODO change later
                bundle.putString(ConstantsDirectory.ENQUIRY_ID, enquiry?.enquiryId?.toString())
                bundle.putString(ConstantsDirectory.ENQUIRY_STATUS_FLAG, "2")
                intent.putExtras(bundle)
                context?.startActivity(intent)
        }


        var image = enquiry?.productImages
        val imgArrSplit = image?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        var first_image = imgArrSplit?.get(0)

        if(enquiry?.productType == "Custom Product"){
            holder?.brandName?.text =enquiry?.brandName
            url = Utility.getCustomProductImagesUrl(enquiry?.productId, first_image)
        }else{
            holder?.brandName?.text =enquiry?.brandName
            url = Utility.getProductsImagesUrl(enquiry?.productId, first_image)
        }
        context?.let { ImageSetter.setImage(it, url!!,holder?.productImage) }

//        holder?.brandName?.text = enquiry?.ProductBrandName

        holder?.enquiryCode?.text = enquiry?.orderCode

        if(enquiry?.productName != ""){
            holder?.productName?.text = enquiry?.productName
        }else{
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if(it.first == enquiry?.weftYarnId){
                    weft = it.second
                }
                if(it.first == enquiry?.warpYarnId){
                    warp = it.second
                }
                if(it.first == enquiry?.extraWeftYarnId){
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if(it.first == enquiry?.productCategoryId){
                    prodCategory = it.second
                }
            }
            var fp = SpannableString("${prodCategory} / ")
            var sp = "${warp} X ${weft} X ${extraweft ?: "N.A"}"
            fp.setSpan(context?.let { ContextCompat.getColor(it, R.color.black_text) }?.let {
                ForegroundColorSpan(
                    it
                )
            }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder?.productName?.text = fp
            holder?.productName?.append(sp)
        }

        when(enquiry?.enquiryStageId){
            1L -> {
                context?.let {
                    ContextCompat.getColor(
                        it,R.color.black_text)
                }?.let { holder.enquiryColor.setBackgroundColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it,R.color.black_text)
                }?.let { holder.enquiryStageStatus.setTextColor(it) }
            }

            2L,3L,4L,5L -> {
                context?.let {
                    ContextCompat.getColor(
                        it,R.color.tab_details_selected_text)
                }?.let { holder.enquiryColor.setBackgroundColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it,R.color.tab_details_selected_text)
                }?.let { holder.enquiryStageStatus.setTextColor(it) }
            }

            6L,7L,8L,9L,10L -> {
                context?.let {
                    ContextCompat.getColor(
                        it,R.color.dark_green)
                }?.let { holder.enquiryColor.setBackgroundColor(it) }

                context?.let {
                    ContextCompat.getColor(
                        it,R.color.dark_green)
                }?.let { holder.enquiryStageStatus.setTextColor(it) }

            }
        }

        var status : String ?= ""
        when(enquiry?.productStatusId){
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

        var enquiryStage : String ?= ""
        var stageList = Utility.getEnquiryStagesData()
        Log.e("enqdata", "List : $stageList")
        stageList.forEach {
            if(it.first == enquiry?.enquiryStageId){
                enquiryStage = it.second
            }
        }
        holder?.enquiryStageStatus.text = enquiryStage

        if(enquiry?.isMoqSend == null && enquiry?.enquiryStageId == 1L){
            holder.reqDoc?.visibility = View.VISIBLE
            holder.reqDoc?.text = "Requesting MOQ"
        }else if(enquiry?.isPiSend == null && enquiry?.enquiryStageId == 2L){
            holder.reqDoc?.visibility = View.VISIBLE
            holder.reqDoc?.text = "Awaiting PI"

        }else{
            holder.reqDoc?.visibility = View.GONE

        }

//        if(enquiry?.isPiSend == 1L && enquiry?.enquiryStageId == 2L){
//            holder.reqDoc?.visibility = View.GONE
//        }else{
//            holder.reqDoc?.visibility = View.VISIBLE
//            holder.reqDoc?.text = "Requesting PI"
//        }
//        if(enquiry?.isMoqSend == 1L || enquiry?.isPiSend == 1L){
//            holder?.reqDoc?.visibility = View.VISIBLE
//        }else{
//            holder?.reqDoc?.visibility = View.GONE
//        }
    }

}