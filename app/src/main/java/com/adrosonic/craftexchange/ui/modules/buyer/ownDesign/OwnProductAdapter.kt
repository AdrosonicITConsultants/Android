package com.adrosonic.craftexchange.ui.modules.buyer.ownDesign


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
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.ui.modules.buyer.wishList.WishlistAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.like.LikeButton
import io.realm.RealmResults


class OwnProductAdapter(
    private val context: Context,
    private var categoryProduct: RealmResults<BuyerCustomProduct>?
) : RecyclerView.Adapter<OwnProductAdapter.MyViewHolder>() {

    interface OwnProductListUpdatedListener {
        fun onDeleted(productId:Long)
    }
    interface EnquiryGeneratedListener{
        fun onEnquiryGenClick(productId: Long,isCustom : Boolean)
    }


    var listener: OwnProductListUpdatedListener? = null
    var enqListener: EnquiryGeneratedListener?=null

    private var wishlistItems=categoryProduct

    fun updateWishlist(newFolders: RealmResults<BuyerCustomProduct>?){
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_custom_product, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pos=position
        var product = categoryProduct?.get(position)
        holder.productTitle.text = product?.productCategoryDscrp+"/ "+product?.dyeDsrcp
        holder.productDescription.text = product?.productSpe
        holder.productAvailableText.text ="Created On: ${Utility.returnDisplayDate(product?.createdOn?:"")}"

        var image = ProductPredicates.getProductDisplayImage(product?.id)
        var url = Utility.getCustomProductImagesUrl(product?.id,image?.imageName)
        context.let { ImageSetter.setImage(it,url,holder.productImage) }
        holder.btnGenerateEnquiry.setOnClickListener {

        }
        holder.wishlistButton.setOnClickListener {
            listener?.onDeleted(product?.id?:0)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context.ownDesignIntent(product?.id?:0))
            val bundle = Bundle()
            bundle.putString(ConstantsDirectory.PRODUCT_ID, product?.id?.toString())
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        holder.btnGenerateEnquiry.setOnClickListener {
            generateEnquiry(product?.id?:0,true)
        }

    }

    override fun getItemCount(): Int {
        return categoryProduct?.size?:0
    }

    fun generateEnquiry(productId : Long, isCustom : Boolean){
        enqListener?.onEnquiryGenClick(productId,isCustom)
    }


}