package com.adrosonic.craftexchange.ui.modules.buyer.wishList


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.like.LikeButton
import io.realm.RealmResults


class WishlistAdapter(
    private val context: Context,
    private var categoryProduct: RealmResults<ProductCatalogue>?
) : RecyclerView.Adapter<WishlistAdapter.MyViewHolder>() {

    interface WishListUpdatedListener {
        fun onSelected(productId:Long,isWishListed:Long)
    }

    var listener: WishListUpdatedListener? = null
    private var wishlistItems=categoryProduct

    fun updateWishlist(newFolders: RealmResults<ProductCatalogue>?){
        this.wishlistItems=newFolders
        this.notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var productImage: ImageView
        var wishlistButton: ImageView
        var productTitle: TextView
        var productAvailableText: TextView
        var productDescription: TextView
        var btnViewMore: Button
        var btnGenerateEnquiry: Button

        init {
            productImage = view.findViewById(R.id.product_image)
            wishlistButton = view.findViewById(R.id.wishlist_button)
            productTitle = view.findViewById(R.id.product_title)
            productAvailableText = view.findViewById(R.id.product_available_text)
            productDescription = view.findViewById(R.id.product_description)
            btnViewMore = view.findViewById(R.id.btn_view_more)
            btnGenerateEnquiry = view.findViewById(R.id.btn_generate_enquiry)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_wishlist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pos=position
        var product = categoryProduct?.get(position)
        holder.productTitle.text = product?.productTag
        var status : String ?= ""
        when(product?.productStatusId){
            2L-> {
                status = ConstantsDirectory.AVAILABLE_IN_STOCK
                holder.productAvailableText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.light_green)
                }?.let { holder.productAvailableText.setTextColor(it) }
            }
            1L -> {
                status = ConstantsDirectory.MADE_TO_ORDER
                holder.productAvailableText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_orange)
                }?.let { holder.productAvailableText.setTextColor(it) }
            }
        }
        holder.productDescription.text = product?.product_spe

        var image = ProductPredicates.getProductDisplayImage(product?.productId)
        var url = Utility.getProductsImagesUrl(product?.productId,image?.imageName)
        context?.let { ImageSetter.setImage(it,url,holder.productImage) }

        holder.btnViewMore.setOnClickListener {
            val intent = Intent(context?.catalogueProductDetailsIntent())
            val bundle = Bundle()
            bundle.putString(ConstantsDirectory.PRODUCT_ID, product?.productId?.toString())
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context?.catalogueProductDetailsIntent())
            val bundle = Bundle()
            bundle.putString(ConstantsDirectory.PRODUCT_ID, product?.productId?.toString())
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }
        holder.wishlistButton.setOnClickListener {
            //todo show dialog
//            if(product?.isWishlisted!!.equals(1))
                removeFromWishlist(pos,0,product?.productId?:0)
//            else  removeFromWishlist(pos,1,product.productId?:0)
        }
    }

    override fun getItemCount(): Int {
        return categoryProduct?.size?:0
    }

fun removeFromWishlist(pos:Int,isWishListed: Long,productId:Long){
    notifyItemRangeChanged(pos, categoryProduct?.size?:0)
    listener?.onSelected(productId, isWishListed)
}

}