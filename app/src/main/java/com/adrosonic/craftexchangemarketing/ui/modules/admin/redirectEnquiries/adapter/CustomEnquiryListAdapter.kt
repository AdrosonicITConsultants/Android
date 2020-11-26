package com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries.adapter


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.AdminProductCatalogue
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.CustomEnquiries
import com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries.RedirectEnqProductDetailsFragment
import com.adrosonic.craftexchangemarketing.ui.modules.products.ViewProductDetailsFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.wajahatkarim3.easyvalidation.core.collection_ktx.containsList
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import java.text.SimpleDateFormat
import java.util.*


class CustomEnquiryListAdapter(
    private val context: Context,
    private var list: ArrayList<CustomEnquiries>?,
    val roleId:Long
) : RecyclerView.Adapter<CustomEnquiryListAdapter.MyViewHolder>() {

    private var enqList=list

    fun updateProducts(newData: ArrayList<CustomEnquiries>?){
        this.enqList=newData
        this.notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtDate: TextView
        var txtArtisanId: TextView
        var txtBrandName: TextView
        var txtProductCat: TextView
        var txtWeave: TextView

        init {
            txtDate = view.findViewById(R.id.txtDate)
            txtArtisanId = view.findViewById(R.id.txtArtisanId)
            txtBrandName = view.findViewById(R.id.txtBrandName)
            txtProductCat = view.findViewById(R.id.txtProductCat)
            txtWeave = view.findViewById(R.id.txtWeave)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_redirect_enq_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pos=position
        var enq = enqList?.get(position)

        holder.txtArtisanId.text=enq?.code?:"NA"
        holder.txtBrandName.text=enq?.companyName?:"NA"
        holder.txtProductCat.text=enq?.productCategory?:"NA"
        holder.txtWeave.text=enq?.weave?:"NA"
        holder.txtDate.text =enq?.date?:"NA"//"${cal.get(Calendar.DAY_OF_MONTH)}-"+ cal.get(Calendar.MONTH)+"-"+ cal.get( Calendar.YEAR)

        holder.itemView.setOnClickListener {
            if(Utility.checkIfInternetConnected(context)) {
                    (context as AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.admin_home_container, RedirectEnqProductDetailsFragment.newInstance(enq?.productId?:0,  roleId,enq?.id?:0 ) )
                        ?.addToBackStack(null)
                        ?.commit()
            }else Utility.displayMessage(context.getString(R.string.no_internet_connection),context)
        }
    }

    override fun getItemCount(): Int {
        return enqList?.size?:0
    }
}