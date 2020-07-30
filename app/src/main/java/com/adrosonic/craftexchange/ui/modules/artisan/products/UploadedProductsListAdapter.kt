package com.adrosonic.craftexchange.ui.modules.artisan.products

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ItemArtisanProductBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility

class UploadedProductsListAdapter(var context: Context?, private var artisanProducts: List<ProductCard>) : RecyclerView.Adapter<UploadedProductsListAdapter.ViewHolder>(){

    inner class ViewHolder(var binding: ItemArtisanProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(artisanProduct: ProductCard){
            binding.itemArtisanProduct = artisanProduct
//            binding.event = this@ArtisanProductAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemArtisanProductBinding = DataBindingUtil.inflate(inflater, R.layout.item_artisan_product,parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = artisanProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = artisanProducts[position]
        holder.bind(product)
        holder.binding.productTitle.text = product.productTitle
        holder.binding.productDescription.text = product.productDescription
        var status : String ?= ""
        when(product.statusId){
            2.toLong() -> {
                status = context?.getString(R.string.in_stock)
                holder.binding.availabilityText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green)
                }?.let { holder.binding.availabilityText.setTextColor(it) }
            }
            1.toLong() -> {
                status = context?.getString(R.string.exclusively_enter)
                var mto = SpannableString(ConstantsDirectory.MADE_TO_ORDER)
                mto.setSpan(context?.let { ContextCompat.getColor(it,R.color.light_green) }?.let {
                    ForegroundColorSpan(
                        it
                    )
                }, 0, mto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                holder.binding.availabilityText.text = status
                holder.binding.availabilityText.append(mto)
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta)
                }?.let { holder.binding.availabilityText.setTextColor(it) }
            }
        }

        var image = ProductPredicates.getProductDisplayImage(product.productId)
        var url = Utility.getProductsImagesUrl(product.productId,image?.imageName)
        context?.let { ImageSetter.setImage(it,url,holder.binding.productImage) }
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(artisanProducts: List<ProductCard>) {
        this.artisanProducts = artisanProducts
        notifyDataSetChanged()
    }




}