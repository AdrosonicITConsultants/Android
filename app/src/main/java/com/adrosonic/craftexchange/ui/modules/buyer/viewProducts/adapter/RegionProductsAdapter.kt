package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.databinding.ItemProductDescListBinding
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.Cluster
import com.adrosonic.craftexchange.ui.interfaces.ClusterProductClick
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import java.util.*

class RegionProductsAdapter(var context: Context?, private var regionProduct: List<ProductCard>) : RecyclerView.Adapter<RegionProductsAdapter.ViewHolder>(),
    ClusterProductClick {

    inner class ViewHolder(val binding: ItemProductDescListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(regionProduct: ProductCard){
            binding.regionProducts = regionProduct
            binding.regionEvent = this@RegionProductsAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemProductDescListBinding = DataBindingUtil.inflate(inflater, R.layout.item_product_desc_list,parent, false)
        return ViewHolder(binding)    }

    override fun getItemCount(): Int = regionProduct.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = regionProduct[position]
        var rnd = Random()
        var currentColor = Color.argb(200, rnd.nextInt(202), rnd.nextInt(256), rnd.nextInt(256))
        holder.bind(product)
        holder.binding.productDescBck.setBackgroundColor(currentColor)
        holder.binding.productTitle.text = product.productTitle
        var status : String ?= ""
        when(product.statusId){
            1.toLong() -> {
                status = ConstantsDirectory.AVAILABLE_IN_STOCK
                holder.binding.productAvailableText.text = status
                holder.binding.productAvailableText.resources.getColor(R.color.light_green)
            }
            2.toLong() -> {
                status = ConstantsDirectory.MADE_TO_ORDER
                holder.binding.productAvailableText.text = status
                holder.binding.productAvailableText.resources.getColor(R.color.dark_orange)
            }
        }
        holder.binding.productDescription.text = product.productDescription
//        holder.binding.prodImg.setBackgroundColor(currentColor) // TODO : to be commented later
//        holder.binding.prodText.text= product.productDesc
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(regionProduct: List<ProductCard>) {
        this.regionProduct = regionProduct
        notifyDataSetChanged()
    }

    fun performFilter(filter : String?){}

    override fun onItemClick(list: Cluster) {
        TODO("Not yet implemented")
    }

}