package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.databinding.ItemProductDescListBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandDetails
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.ui.interfaces.BrandProductClick
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.LandingViewModel
import com.adrosonic.craftexchange.viewModels.WishlistViewModel
import com.like.LikeButton
import com.like.OnLikeListener
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*
import javax.security.auth.callback.Callback

class BrandProductsAdapter(var context: Context?, private var brandProduct: List<ProductCard>) : RecyclerView.Adapter<BrandProductsAdapter.ViewHolder>(){


    inner class ViewHolder(val binding: ItemProductDescListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(brandProduct: ProductCard){
            binding.brandProducts = brandProduct
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
        var product = brandProduct[position]
        var rnd = Random()
        var currentColor = Color.argb(200, rnd.nextInt(202), rnd.nextInt(256), rnd.nextInt(256))
        holder.bind(product)
//        holder.binding.productDescBck.setBackgroundColor(currentColor)
        holder.binding.productTitle.text = product.productTitle
        var status: String? = ""
        when (product.statusId) {
            2.toLong() -> {
                status = ConstantsDirectory.AVAILABLE_IN_STOCK
                holder.binding.productAvailableText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.light_green
                    )
                }?.let { holder.binding.productAvailableText.setTextColor(it) }
            }
            1.toLong() -> {
                status = ConstantsDirectory.MADE_TO_ORDER
                holder.binding.productAvailableText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_orange
                    )
                }?.let { holder.binding.productAvailableText.setTextColor(it) }
            }
        }
        holder.binding.productDescription.text = product.productDescription

        var image = ProductPredicates.getProductDisplayImage(product.productId)
        var url = Utility.getProductsImagesUrl(product.productId, image?.imageName)

        context?.let { ImageSetter.setImage(it, url, holder.binding.productImage) }

        holder.binding.btnViewMore.setOnClickListener {
            val intent = Intent(context?.catalogueProductDetailsIntent())
            val bundle = Bundle()
            bundle.putString(ConstantsDirectory.PRODUCT_ID, product.productId?.toString())
            intent.putExtras(bundle)
            context?.startActivity(intent)
        }

        holder.binding.wishlistButton.isLiked = product.isWishlisted == 1L
        var wishVM = WishlistViewModel(context?.applicationContext as Application)
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        holder.binding?.wishlistButton?.setOnLikeListener(object: OnLikeListener{
            override fun liked(likeButton: LikeButton) {
                product.productId?.let { it1 ->
                    CraftExchangeRepository
                        .getWishlistService()
                        .addToWishlist(token, it1)
                        .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                t.printStackTrace()
                                Log.e("AddToWishlist failure ","${t.printStackTrace()}")
                                Utility.displayMessage("Error while adding to wishlist", context!!)
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: retrofit2.Response<ResponseBody>) {
                                if(response.isSuccessful){
                                    Log.e(WishlistViewModel.TAG,"addToWishlist :"+response.body())
                                    WishlistPredicates.updateProductWishlisting(product.productId,1L)
                                    Utility.displayMessage("Added to wishlist :-)",context!!)
                                    holder.binding.wishlistButton.isLiked = true
                                }else{
                                    Log.e(WishlistViewModel.TAG,"addToWishlist "+response.body())
                                    Utility.displayMessage("Error while adding to wishlist",context!!)
                                }
                            }

                        })

            }}
            override fun unLiked(likeButton:LikeButton) {
                product.productId?.let { it1 ->
                    CraftExchangeRepository
                        .getWishlistService()
                        .deleteProductsInWishlist(token, it1)
                        .enqueue(object: Callback, retrofit2.Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                t.printStackTrace()
                                Log.e("AddToWishlist failure ","${t.printStackTrace()}")

                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: retrofit2.Response<ResponseBody>) {
                                Log.e(WishlistViewModel.TAG,"onResponse :"+response.code())
                                Log.e(WishlistViewModel.TAG,"onResponse :"+response.isSuccessful)
                                Log.e(WishlistViewModel.TAG,"onResponse :"+call.request().url)
                                if(response.isSuccessful){
                                    Log.e(WishlistViewModel.TAG,"addToWishlist :"+response.body())
                                    WishlistPredicates.updateProductWishlisting(product.productId,0L)
                                    Utility.displayMessage("Removed from wishlist :-(",context!!)
                                    holder.binding.wishlistButton.isLiked = false

                                }else{
                                    Log.e(WishlistViewModel.TAG,"addToWishlist "+response.body())

                                }
                            }
                        })
                }
            }
        })




//Todo
//        var coordinator = context?.let { SyncCoordinator(it) }
//        holder.binding.wishlistButton.setOnClickListener {
//            coordinator?.performLocallyAvailableActions()
//        }

    }


    internal fun setProducts(brandProduct: List<ProductCard>) {
        this.brandProduct = brandProduct
        notifyDataSetChanged()
    }




}