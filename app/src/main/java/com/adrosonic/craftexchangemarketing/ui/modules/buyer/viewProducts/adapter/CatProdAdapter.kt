package com.adrosonic.craftexchangemarketing.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.WishlistPredicates
import com.adrosonic.craftexchangemarketing.databinding.ItemProductDescListBinding
import com.adrosonic.craftexchangemarketing.syncManager.SyncCoordinator
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.like.LikeButton
import com.like.OnLikeListener
import io.realm.RealmResults

class CatProdAdapter(var context: Context?, private var regionProduct: RealmResults<ProductCatalogue>?) : RecyclerView.Adapter<CatProdAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProductDescListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(regionProduct: ProductCatalogue){
            binding.regionProducts = regionProduct
            binding.executePendingBindings()
        }
    }

    interface EnquiryGeneratedListener{
        fun onEnquiryGenClick(productId: Long,isCustom : Boolean)
    }

    var enqListener: EnquiryGeneratedListener?=null


    override fun getItemCount(): Int {
        return regionProduct?.size?:0
    }

    fun updateProductList(newList: RealmResults<ProductCatalogue>?){
        if (newList != null) {
            this.regionProduct=newList
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemProductDescListBinding = DataBindingUtil.inflate(inflater, R.layout.item_product_desc_list,parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = regionProduct?.get(position)

        product?.let { holder.bind(it) }
//        holder.binding.productDescBck.setBackgroundColor(currentColor)
        holder.binding.productTitle.text = product?.productTag
        var status : String ?= ""
        when(product?.productStatusId){
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
        holder.binding.productDescription.text = product?.product_spe

        var image = ProductPredicates.getProductDisplayImage(product?.productId)
        var url = Utility.getProductsImagesUrl(product?.productId,image?.imageName)
        context?.let { ImageSetter.setImage(it,url,holder.binding.productImage) }

        holder.binding.btnViewMore.setOnClickListener {
            val intent = Intent(context?.catalogueProductDetailsIntent())
            val bundle = Bundle()
            bundle.putString(ConstantsDirectory.PRODUCT_ID, product?.productId?.toString())
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }

        holder.binding.productImage.setOnClickListener {
            val intent = Intent(context?.catalogueProductDetailsIntent())
            val bundle = Bundle()
            bundle.putString(ConstantsDirectory.PRODUCT_ID, product?.productId?.toString())
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }

        holder.binding.wishlistButton.isLiked = product?.isWishlisted == 1L
        holder.binding.wishlistButton.setOnLikeListener(object: OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                WishlistPredicates.updateProductWishlisting(product?.productId,1L,1L)
                if(Utility.checkIfInternetConnected(context!!)){
                    val coordinator = SyncCoordinator(context!!)
                    coordinator.performLocallyAvailableActions()
                }
            }

            override fun unLiked(likeButton: LikeButton) {
                WishlistPredicates.updateProductWishlisting(product?.productId,0L,1L)
                if(Utility.checkIfInternetConnected(context!!)){
                    val coordinator = SyncCoordinator(context!!)
                    coordinator.performLocallyAvailableActions()
                }
            }
        })

        holder.binding.btnGenerateEnquiry.setOnClickListener {
            generateEnquiry(product?.productId ?:0,false)
        }
    }

    fun generateEnquiry(productId : Long, isCustom : Boolean){
        enqListener?.onEnquiryGenClick(productId,isCustom)
    }
}