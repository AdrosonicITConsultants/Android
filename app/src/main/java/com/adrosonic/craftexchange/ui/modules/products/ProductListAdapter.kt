package com.adrosonic.craftexchange.ui.modules.products

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.ProductListRecyclerView
import com.adrosonic.craftexchange.databinding.ProductCategoryItemBinding
import com.adrosonic.craftexchange.ui.interfaces.ClickingInterface

class ProductListAdapter(var context: Context?, private var products: List<ProductListRecyclerView>) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>(), ClickingInterface {

    inner class ViewHolder(val binding: ProductCategoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductListRecyclerView){
            binding.product = product
            binding.event = this@ProductListAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ProductCategoryItemBinding = DataBindingUtil.inflate(inflater, R.layout.product_category_item,parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        var product = products[position]
        holder.bind(product)
        holder.binding.prodText.text = product.productName
        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(products: List<ProductListRecyclerView>) {
        this.products = products
        notifyDataSetChanged()
    }

    override fun onItemClick(product: ProductListRecyclerView) {
        TODO("Not yet implemented")
    }
}

