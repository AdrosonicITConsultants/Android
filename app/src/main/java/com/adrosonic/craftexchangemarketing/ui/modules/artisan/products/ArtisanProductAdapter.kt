package com.adrosonic.craftexchangemarketing.ui.modules.artisan.products

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.databinding.ItemArtisanProductCategoryBinding
import com.adrosonic.craftexchangemarketing.ui.interfaces.ArtisanProductClick
//import com.adrosonic.craftexchangemarketing.ui.modules.artisan.landing.ArtisanLandingActivity
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import io.realm.RealmResults

class ArtisanProductAdapter(var context: Context?, private var artisanProducts: RealmResults<ArtisanProducts>?) : RecyclerView.Adapter<ArtisanProductAdapter.ViewHolder>(),
    ArtisanProductClick {

    inner class ViewHolder(val binding: ItemArtisanProductCategoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(artisanProduct: ArtisanProducts){
            binding.artisanProduct = artisanProduct
            binding.event = this@ArtisanProductAdapter
            binding.executePendingBindings()
        }
    }

    fun updateCategoryList(newList: RealmResults<ArtisanProducts>?){
        this.artisanProducts=newList
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemArtisanProductCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.item_artisan_product_category,parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return artisanProducts?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        var product = artisanProducts?.get(position)
        product?.let { holder.bind(it) }
        holder.binding.prodText.text = product?.productCategoryDesc
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }


    override fun onItemClick(list: ArtisanProducts) {
        var bundle = Bundle()
        list.artisanId?.let { bundle.putLong(ConstantsDirectory.ARTISAN_ID, it) }
        bundle.putString(ConstantsDirectory.PRODUCT_CATEGORY,list.productCategoryDesc)
        list.productCategoryId?.let { bundle.putLong(ConstantsDirectory.PRODUCT_CATEGORY_ID, it) }
        var frag2 = ArtisanUploadedProductsFragment()
        frag2.arguments = bundle
//        var activity = context as ArtisanLandingActivity
//        activity.supportFragmentManager.beginTransaction()
//            .replace(R.id.admin_home_container, frag2)
//            .addToBackStack(null)
//            .commit()
    }

}

