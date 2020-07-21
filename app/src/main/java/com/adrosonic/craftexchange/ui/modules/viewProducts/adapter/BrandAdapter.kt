package com.adrosonic.craftexchange.ui.modules.viewProducts.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ItemBrandProductsBinding
import com.adrosonic.craftexchange.repository.data.response.viewProducts.BrandDetails
import com.adrosonic.craftexchange.ui.interfaces.BrandProductClick
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchange.ui.modules.viewProducts.productlists.BrandProdListFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter

class BrandAdapter(var context: Context?, private var brandDetails: List<BrandDetails>) : RecyclerView.Adapter<BrandAdapter.ViewHolder>(),
    BrandProductClick {

    var url : String ?= ""

    inner class ViewHolder(val binding: ItemBrandProductsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(brandDetails: BrandDetails){
            binding.brandDetails = brandDetails
            binding.event = this@BrandAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemBrandProductsBinding = DataBindingUtil.inflate(inflater, R.layout.item_brand_products,parent, false)
        return ViewHolder(binding)    }

    override fun getItemCount(): Int = brandDetails.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = brandDetails[position]
        holder.bind(product)
        if(product.logo != null){
            url = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${product.artisanId}/CompanyDetails/Logo/${product.logo}"
        }else{
            url = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${product.artisanId}/ProfilePics/${product.profilePic}"
        }

        ImageSetter.setImage(context!!,url!!,holder.binding.prodImg,R.drawable.artisan_logo_placeholder
        ,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)

        holder.binding.prodText.text = product.companyName ?: product.firstName
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(categoryDetails: List<BrandDetails>) {
        this.brandDetails = categoryDetails
        notifyDataSetChanged()
    }

    override fun onItemClick(list: BrandDetails) {
        Toast.makeText(context,"$list", Toast.LENGTH_SHORT).show()
        var bundle = Bundle()
        bundle.putString(ConstantsDirectory.ARTISAN_ID,list.artisanId.toString())
        var frag2 = BrandProdListFragment()
        frag2.arguments = bundle
        var activity = context as BuyerLandingActivity
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.buyer_home_container, frag2)
            .addToBackStack(null)
            .commit()
    }


//    override fun onItemClick(artisanProduct: ArtisanProductList) {
//        TODO("Not yet implemented")
//    }

}