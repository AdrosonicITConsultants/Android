package com.adrosonic.craftexchange.ui.modules.buyer.search.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.search.SearchProduct
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.singleProduct.SingleProductDetails
import com.adrosonic.craftexchange.repository.data.response.search.SearchProductData
import com.adrosonic.craftexchange.repository.data.response.search.SearchProductResponse
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.ui.modules.buyer.wishList.WishlistAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.like.LikeButton
import com.like.OnLikeListener
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class BuyerSearchAdapter(private val mContext : Context,
                         private var productList: ArrayList<SearchProductData>?): RecyclerView.Adapter<BuyerSearchAdapter.MyViewHolder>()  {

    interface WishListUpdatedListener {
        fun onSelected(productId:Long,isWishListed:Long)
    }

    interface EnquiryGeneratedListener{
        fun onEnquiryGenClick(productId: Long,isCustom : Boolean)
    }

    var wishlistener: WishListUpdatedListener? = null
    var enqListener: EnquiryGeneratedListener?=null
    private var wishlistItems=productList

    fun updateWishlist(newFolders: ArrayList<SearchProductData>?){
        this.wishlistItems=newFolders
        this.notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var productImage: ImageView = view.findViewById(R.id.product_image)
        var wishlistButton: LikeButton = view.findViewById(R.id.wishlist_button)
        var productTitle: TextView = view.findViewById(R.id.product_title)
        var productAvailableText: TextView = view.findViewById(R.id.product_available_text)
        var productDescription: TextView = view.findViewById(R.id.product_description)
        var btnViewMore: Button = view.findViewById(R.id.btn_search_view_more)
        var btnGenerateEnquiry: Button = view.findViewById(R.id.btn_search_generate_enquiry)
        var collectionLogo : ImageView = view.findViewById(R.id.design_logo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BuyerSearchAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_buyer_search_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productList?.size?:0
    }

    override fun onBindViewHolder(holder: BuyerSearchAdapter.MyViewHolder, position: Int) {
        val pos=position
        var product = productList?.get(position)
        holder.productTitle.text = product?.tag
        var status : String ?= ""
        when(product?.status){
            2L-> {
                status = ConstantsDirectory.AVAILABLE_IN_STOCK
                holder.productAvailableText.text = status
                mContext?.let {
                    ContextCompat.getColor(
                        it, R.color.light_green)
                }.let { holder.productAvailableText.setTextColor(it) }
            }
            1L -> {
                status = ConstantsDirectory.MADE_TO_ORDER
                holder.productAvailableText.text = status
                mContext?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_orange)
                }.let { holder.productAvailableText.setTextColor(it) }
            }
        }
        holder.productDescription.text = product?.productDesc

        when(product?.madeWithAnthran){
            0L -> {
                holder?.collectionLogo?.setImageResource(R.drawable.ic_artisan_self_design_icon)
            }
            1L -> {
                holder?.collectionLogo?.setImageResource(R.drawable.ic_antaran_co_design_icon)
            }
        }

        var image = product?.images
        val imgArrSplit = image?.split((",").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        var first_image = imgArrSplit?.get(0)
        var url = Utility.getProductsImagesUrl(product?.id, first_image)
        mContext.let { ImageSetter.setImage(it,url,holder?.productImage) }

        holder?.wishlistButton?.setOnLikeListener(object: OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                WishlistPredicates.updateProductWishlisting(product?.id,1L,1L)
                if(Utility.checkIfInternetConnected(mContext)){
                    val coordinator = SyncCoordinator(mContext)
                    coordinator.performLocallyAvailableActions()
                }
            }

            override fun unLiked(likeButton: LikeButton) {
                WishlistPredicates.updateProductWishlisting(product?.id,0L,1L)
                if(Utility.checkIfInternetConnected(mContext)){
                    val coordinator = SyncCoordinator(mContext)
                    coordinator.performLocallyAvailableActions()
                }
            }
        })

        holder.btnViewMore.setOnClickListener {
        //TODO : change this implementation later

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        product?.id?.let { it1 ->
                CraftExchangeRepository
                    .getWishlistService()
                    .getSingleProductDetails(token, it1.toInt())
                    .enqueue(object : Callback, retrofit2.Callback<SingleProductDetails> {
                        override fun onFailure(call: Call<SingleProductDetails>, t: Throwable) {
                            t.printStackTrace()
                            Log.e("prodDetails","Failure : "+t.printStackTrace())
        //                        listener?.onProdFetchFail()
                        }

                        override fun onResponse(
                            call: Call<SingleProductDetails>, response: Response<SingleProductDetails>
                        ) {
                            if (response.body()?.valid == true) {
                                val response=response.body()?.data
                                if(response != null){
                                    WishlistPredicates.insertSingleProduct(response)
                                    val intent = Intent(mContext.catalogueProductDetailsIntent())
                                    val bundle = Bundle()
                                    bundle.putString(ConstantsDirectory.PRODUCT_ID, product?.id?.toString())
                                    intent.putExtras(bundle)
                                    mContext.startActivity(intent)
                                }
                            } else {
                                Log.e("prodDetails","Failure")
//                                listener?.onProdFetchFail()

                            }
                        }
                    })
            }

//            val intent = Intent(mContext.catalogueProductDetailsIntent())
//            val bundle = Bundle()
//            bundle.putString(ConstantsDirectory.PRODUCT_ID, product?.id?.toString())
//            intent.putExtras(bundle)
//            mContext.startActivity(intent)
        }


        holder.btnGenerateEnquiry.setOnClickListener {
            generateEnquiry(product?.id?:0,false)
        }
    }

    fun generateEnquiry(productId : Long, isCustom : Boolean){
        enqListener?.onEnquiryGenClick(productId,isCustom)
    }

}