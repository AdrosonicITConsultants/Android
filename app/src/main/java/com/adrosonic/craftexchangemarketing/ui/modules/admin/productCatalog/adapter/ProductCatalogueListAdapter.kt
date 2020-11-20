package com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog.adapter


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
import com.adrosonic.craftexchangemarketing.ui.modules.products.ViewProductDetailsFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.wajahatkarim3.easyvalidation.core.collection_ktx.containsList
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import java.text.SimpleDateFormat
import java.util.*


class ProductCatalogueListAdapter(
    private val context: Context,
    private var productCatalogue: RealmResults<AdminProductCatalogue>?
) : RecyclerView.Adapter<ProductCatalogueListAdapter.MyViewHolder>() {

    private var productList=productCatalogue

    fun updateProducts(newFolders: RealmResults<AdminProductCatalogue>?){
        this.productList=newFolders
        this.notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_prod: ImageView
        var img_availabilty: ImageView
        var txt_prod_name: TextView
        var txt_prod_code: TextView
        var txt_brand: TextView
        var txt_cluster: TextView
        var txt_date: TextView
        var txt_total_orders: TextView

        init {
            img_prod = view.findViewById(R.id.img_prod)
            img_availabilty = view.findViewById(R.id.img_availabilty)
            txt_prod_name = view.findViewById(R.id.txt_prod_name)
            txt_prod_code = view.findViewById(R.id.txt_prod_code)
            txt_brand = view.findViewById(R.id.txt_brand)
            txt_cluster = view.findViewById(R.id.txt_cluster)
            txt_date = view.findViewById(R.id.txt_date)
            txt_total_orders = view.findViewById(R.id.txt_total_orders)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product_catalogue, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pos=position
        var product = productList?.get(position)
        holder.txt_prod_name.text = product?.name
        holder.txt_prod_code.text = product?.code
        holder.txt_brand.text = product?.brand
        holder.txt_cluster.text = product?.category
        val cal: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH)
        cal.setTime(sdf.parse(product?.dateAdded))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.txt_date.setText(Html.fromHtml("<b>Date Added:</b><br>"+"${cal.get(Calendar.DAY_OF_MONTH)}-"+ cal.get(Calendar.MONTH)+"-"+ cal.get( Calendar.YEAR), Html.FROM_HTML_MODE_COMPACT));
            holder.txt_total_orders.setText(Html.fromHtml("<b>Total<br>Orders:</b><br>"+product?.count, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.txt_date.setText(Html.fromHtml("<b>Date Added:</b><br>"+"${cal.get(Calendar.DAY_OF_MONTH)}-"+ cal.get(Calendar.MONTH)+"-"+ cal.get( Calendar.YEAR)));
            holder.txt_total_orders.setText(Html.fromHtml("<b>Total<br>Orders:</b><br>"+product?.count));
        }
        var url = Utility.getProductsImagesUrl(product?.id,product?.images)
        Log.e("ArtisanProduct", "url :$url" )

        context.let { ImageSetter.setImage(it,url,holder.img_prod) }

        if(product?.availability.equals(ConstantsDirectory.MADE_TO_ORDER,true)) Utility.setImageResource(context, holder.img_availabilty, R.drawable.ic_made_to_order)
        else Utility.setImageResource(context, holder.img_availabilty, R.drawable.ic_in_stock)
        holder.itemView.setOnClickListener {
                    (context as AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.admin_home_container, ViewProductDetailsFragment.newInstance(product?.id?:0,  false ) )
                        ?.addToBackStack(null)
                        ?.commit()
        }
    }

    override fun getItemCount(): Int {
        return productList?.size?:0
    }
}