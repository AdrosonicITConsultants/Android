package com.adrosonic.craftexchangemarketing.ui.modules.buyer.productDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ItemMoreProductsItemBinding
//import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.ProductCare
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.viewProducts.productCatalogue.ProductImage
import com.adrosonic.craftexchangemarketing.ui.interfaces.MoreProductClick
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility


class MoreProductsRecyclerAdapter(var context: Context?, private var imgList: List<ProductImage>) : RecyclerView.Adapter<MoreProductsRecyclerAdapter.ViewHolder>()
,MoreProductClick{


    inner class ViewHolder(val binding: ItemMoreProductsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(img: ProductImage){
            binding.image = img
            binding.event = this@MoreProductsRecyclerAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMoreProductsItemBinding = DataBindingUtil.inflate(inflater, R.layout.item_more_products_item,parent, false)
        return ViewHolder(binding)    }

    override fun getItemCount(): Int = imgList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = imgList[position]
        holder.bind(product)
        var url = Utility.getProductsImagesUrl(product.productId,product.lable)
        context?.let { ImageSetter.setImage(it,url,holder.binding.moreImg) }
    }

    internal fun setProducts(img: List<ProductImage>) {
        this.imgList = img
        notifyDataSetChanged()
    }

    override fun onItemClick(prod: ProductImage) {
        val intent = Intent(context?.catalogueProductDetailsIntent())
        val bundle = Bundle()
        bundle.putString(ConstantsDirectory.PRODUCT_ID, prod.productId.toString())
        intent.putExtras(bundle)
        context?.startActivity(intent)
    }


}