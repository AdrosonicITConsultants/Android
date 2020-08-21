package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.databinding.ItemProductDescListBinding
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.like.LikeButton
import com.like.OnLikeListener
import java.util.*

class RegionProductsAdapter(var context: Context?, private var regionProduct: List<ProductCard>) : RecyclerView.Adapter<RegionProductsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProductDescListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(regionProduct: ProductCard){
            binding.regionProducts = regionProduct
            binding.executePendingBindings()
        }
    }

    interface EnquiryGeneratedListener{
        fun onEnquiryGenClick(productId: Long,isCustom : Boolean)
    }

    var enqListener: EnquiryGeneratedListener?=null

    override fun getItemCount(): Int = regionProduct.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemProductDescListBinding = DataBindingUtil.inflate(inflater, R.layout.item_product_desc_list,parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = regionProduct[position]
        var rnd = Random()
        var currentColor = Color.argb(200, rnd.nextInt(202), rnd.nextInt(256), rnd.nextInt(256))
        holder.bind(product)
//        holder.binding.productDescBck.setBackgroundColor(currentColor)
        holder.binding.productTitle.text = product.productTitle
        var status : String ?= ""
        when(product.statusId){
            2.toLong() -> {
                status = ConstantsDirectory.AVAILABLE_IN_STOCK
                holder.binding.productAvailableText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.light_green)
                }?.let { holder.binding.productAvailableText.setTextColor(it) }
            }
            1.toLong() -> {
                status = ConstantsDirectory.MADE_TO_ORDER
                holder.binding.productAvailableText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_orange)
                }?.let { holder.binding.productAvailableText.setTextColor(it) }
            }
        }
        holder.binding.productDescription.text = product.productDescription

        var image = ProductPredicates.getProductDisplayImage(product.productId)
        var url = Utility.getProductsImagesUrl(product.productId,image?.imageName)
        context?.let { ImageSetter.setImage(it,url,holder.binding.productImage) }

        holder.binding.btnViewMore.setOnClickListener {
            val intent = Intent(context?.catalogueProductDetailsIntent())
            val bundle = Bundle()
            bundle.putString(ConstantsDirectory.PRODUCT_ID, product.productId?.toString())
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }

        holder.binding.wishlistButton.isLiked = product.isWishlisted == 1L
        holder.binding.wishlistButton.setOnLikeListener(object: OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                WishlistPredicates.updateProductWishlisting(product.productId,1L,1L)
                if(Utility.checkIfInternetConnected(context!!)){
                    val coordinator = SyncCoordinator(context!!)
                    coordinator.performLocallyAvailableActions()
                }
            }

            override fun unLiked(likeButton: LikeButton) {
                WishlistPredicates.updateProductWishlisting(product.productId,0L,1L)
                if(Utility.checkIfInternetConnected(context!!)){
                    val coordinator = SyncCoordinator(context!!)
                    coordinator.performLocallyAvailableActions()
                }
            }
        })

        holder.binding.btnGenerateEnquiry.setOnClickListener {
            generateEnquiry(product.productId ?:0,false)
        }
    }

    internal fun setProducts(regionProduct: List<ProductCard>) {
        this.regionProduct = regionProduct
        notifyDataSetChanged()
    }
    fun generateEnquiry(productId : Long, isCustom : Boolean){
        enqListener?.onEnquiryGenClick(productId,isCustom)
    }


}