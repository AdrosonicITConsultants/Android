package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.adapter
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryData
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.EnquiriesDatabaseActivity
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.EnquiryDetailsActivity
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.gson.Gson

class ELAdapter (var context: Context?, private var enquiries: ArrayList<EnquiryData> , private var type :Long?) : RecyclerView.Adapter<ELAdapter.MyViewHolder>(){
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var EnquiryCode : TextView = view.findViewById(R.id.EnquiryCode)
        var ArtisanBrand : TextView = view.findViewById(R.id.ArtisanBrand)
        var BuyerBrand : TextView = view.findViewById(R.id.BuyerBrand)
        var Amount : TextView = view.findViewById(R.id.Amount)
        var currentStage : TextView = view.findViewById(R.id.currentStage)
        var typeProduct : TextView = view.findViewById(R.id.typeProduct)
        var dateStarted : TextView = view.findViewById(R.id.dateStarted)
        var lastUpdated : TextView = view.findViewById(R.id.lastUpdated)
        var stepsCompleted : TextView = view.findViewById(R.id.stepsCompleted)
        var ProductTypeImage : ImageView = view.findViewById(R.id.ProductTypeImage)
        var totalSteps : TextView = view.findViewById(R.id.totalSteps)
        var ETA : TextView = view.findViewById(R.id.ETA)
        var ProductName : TextView = view.findViewById(R.id.ProductName)
        var layout : ConstraintLayout = view.findViewById(R.id.EnquiryCard)
    }
    fun updateProductList( enquiryList: ArrayList<EnquiryData> ){
        if (enquiryList != null ) {
            this.enquiries=enquiryList
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.enquiry_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(enquiries[position]?.innerCurrenStage == null)
        {
            holder?.currentStage?.text = enquiries[position]?.currenStage
        }
        else{
            holder?.currentStage?.text = enquiries[position]?.innerCurrenStage

        }
        holder?.layout?.setOnClickListener {
            UserConfig.shared.enquiryData= " "
            UserConfig.shared.enquiryData= Gson().toJson(enquiries[position])
            val myIntent = Intent(context, EnquiryDetailsActivity::class.java)
            Log.d("moqcheck", "onBindViewHolder: "+ enquiries[position].eId)
            myIntent.putExtra("enquiryID",enquiries[position].eId)
            myIntent.putExtra("type", 2.toLong())
            context?.startActivity(myIntent)
        }
        holder?.ProductName?.setOnClickListener {
//            Utility.displayMessage("Product Page Opens Here")
        }
        holder?.EnquiryCode?.text = enquiries[position]?.code
        holder?.ArtisanBrand?.text = enquiries[position]?.artisanBrand
        holder?.BuyerBrand?.text = enquiries[position]?.buyerBrand
        if(enquiries[position]?.amount == null)
        {
            holder?.Amount?.text = "NA"
        }
        else{
            holder?.Amount?.text = enquiries[position]?.amount.toString()
        }
        val date = enquiries[position]?.dateStarted?.split("T")?.get(0)
        holder.dateStarted.text = date
        if(enquiries[position]?.lastUpdated == null )
        {
            holder.lastUpdated.text = "NA"
        }
        else{
            val date = enquiries[position]?.lastUpdated?.split("T")?.get(0)
            holder.lastUpdated.text = date
        }
        if(enquiries[position]?.eta == null )
        {
            holder.ETA.text = "NA"
        }
        else{
            val date = enquiries[position]?.eta?.split("T")?.get(0)
            holder.ETA.text = date
        }
        when(enquiries[position]?.madeWithAntharan){
            null->{

            }
            0->{
                holder?.ProductTypeImage?.setImageResource(R.drawable.ic_artisan_self_design_icon)
            }
            1->{
                holder?.ProductTypeImage?.setImageResource(R.drawable.ic_antaran_co_design_icon)
            }

        }
        if(enquiries[position]?.productHistoryId != null)
        {
            holder?.ProductName?.text = enquiries[position]?.historyTag
            when(enquiries[position]?.productHistoryStatus){
                0->{
                    holder?.typeProduct?.text = "Buyer Custom Product"
                    holder?.totalSteps?.text = "/10"
                    holder?.stepsCompleted?.text = enquiries[position]?.currenStageId.toString()

                }
                1 ->{
                    holder?.typeProduct?.text = "Made ro Order"
                    holder?.totalSteps?.text = "/10"
                    holder?.stepsCompleted?.text = enquiries[position]?.currenStageId.toString()

                }
                2->{
                    holder?.typeProduct?.text = "Available in Stock"
                    holder?.totalSteps?.text = "/7"
                    if(enquiries[position]?.currenStageId > 4)
                    {
                        holder?.stepsCompleted?.text = (enquiries[position]?.currenStageId - 3).toString()

                    }
                    else{
                        holder?.stepsCompleted?.text = enquiries[position]?.currenStageId.toString()
                    }

                }
            }

        }
        else{
            holder?.ProductName?.text = enquiries[position]?.tag
            when(enquiries[position]?.productStatus){
                0->{
                    holder?.typeProduct?.text = "Buyer Custom Product"
                    holder?.totalSteps?.text = "/10"
                    holder?.stepsCompleted?.text = enquiries[position]?.currenStageId.toString()

                }
                1 ->{
                    holder?.typeProduct?.text = "Made ro Order"
                    holder?.totalSteps?.text = "/10"
                    holder?.stepsCompleted?.text = enquiries[position]?.currenStageId.toString()

                }
                2->{
                    holder?.typeProduct?.text = "Available in Stock"
                    holder?.totalSteps?.text = "/7"
                    if(enquiries[position]?.currenStageId > 4)
                    {
                        holder?.stepsCompleted?.text = (enquiries[position]?.currenStageId - 3).toString()

                    }
                    else{
                        holder?.stepsCompleted?.text = enquiries[position]?.currenStageId.toString()
                    }

                }
            }
        }






    }


    override fun getItemCount(): Int {
        return enquiries?.size?:0
    }


}