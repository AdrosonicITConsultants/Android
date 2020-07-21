package com.adrosonic.craftexchange.ui.modules.products

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.ArtisanProductList
import com.adrosonic.craftexchange.databinding.ItemArtisanProductCategoryBinding
import com.adrosonic.craftexchange.repository.data.response.viewProducts.brand.Product
import com.adrosonic.craftexchange.ui.interfaces.CategoryProductClick

class ProductListAdapter(var context: Context?, private var artisanProducts: List<ArtisanProductList>) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemArtisanProductCategoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(artisanProduct: ArtisanProductList){
            binding.artisanProduct = artisanProduct
//            binding.event = this@ProductListAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemArtisanProductCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.item_artisan_product_category,parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = artisanProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        var product = artisanProducts[position]
        holder.bind(product)
        holder.binding.prodText.text = product.productName
        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(artisanProducts: List<ArtisanProductList>) {
        this.artisanProducts = artisanProducts
        notifyDataSetChanged()
    }

//    override fun onItemClick(list: Product) {
//        TODO("Not yet implemented")
//    }


}

