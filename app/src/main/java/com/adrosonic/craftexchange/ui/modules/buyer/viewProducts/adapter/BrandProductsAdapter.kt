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
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandDetails
import com.adrosonic.craftexchange.ui.interfaces.BrandProductClick
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import java.util.*

class BrandProductsAdapter(var context: Context?, private var brandProduct: List<ProductCard>) : RecyclerView.Adapter<BrandProductsAdapter.ViewHolder>(),BrandProductClick{

    inner class ViewHolder(val binding: ItemProductDescListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(brandProduct: ProductCard){
            binding.brandProducts = brandProduct
            binding.brandEvent = this@BrandProductsAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemProductDescListBinding = DataBindingUtil.inflate(inflater, R.layout.item_product_desc_list,parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = brandProduct.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var prodproduct = brandProduct[position]
        var rnd = Random()
        var currentColor = Color.argb(200, rnd.nextInt(202), rnd.nextInt(256), rnd.nextInt(256))
        holder.bind(prodproduct)
        holder.binding.productDescBck.setBackgroundColor(currentColor)
        holder.binding.productTitle.text = prodproduct.productTitle
        var status : String ?= ""
        when(prodproduct.statusId){
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
        holder.binding.productDescription.text = prodproduct.productDescription

//        holder.binding.prodImg.setBackgroundColor(currentColor) // TODO : to be commented later
//        holder.binding.prodText.text= product.productDesc
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    fun filterList(filterId : Long?){

    }

    internal fun setProducts(brandProduct: List<ProductCard>) {
        this.brandProduct = brandProduct
        notifyDataSetChanged()
    }


    override fun onItemClick(list: BrandDetails) {
        TODO("Not yet implemented")
    }

}