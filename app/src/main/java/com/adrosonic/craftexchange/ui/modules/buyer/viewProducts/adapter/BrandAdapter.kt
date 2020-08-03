package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ItemBrandProductsBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.BrandDetails
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchange.ui.interfaces.BrandProductClick
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists.BrandProdListFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

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
            url = Utility.getBrandLogoUrl(product.artisanId,product.logo)
        }else{
            url = Utility.getProfilePhotoUrl(product.artisanId,product.profilePic)
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
//        Toast.makeText(context,"$list", Toast.LENGTH_SHORT).show()
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        CraftExchangeRepository
            .getProductService()
            .getProductsByArtisan(token,list.artisanId!!)
            .enqueue(object : Callback, retrofit2.Callback<CatalogueProductsResponse> {
                override fun onFailure(call: Call<CatalogueProductsResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<CatalogueProductsResponse>, response: Response<CatalogueProductsResponse>
                ) {
                    if (response.body()?.valid == true) {
                        ProductPredicates.insertProductsInCatalogue(response.body()?.data?.products)
                    } else {
                        Toast.makeText(context, "${response.body()}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        var bundle = Bundle()
        bundle.putString(ConstantsDirectory.ARTISAN_ID,list.artisanId.toString())
        bundle.putString(ConstantsDirectory.ARTISAN,list.firstName)
        bundle.putString(ConstantsDirectory.COMP_NAME,list.companyName)
        var logo = list.logo ?: ""
        var photo = list.profilePic ?: ""
        bundle.putString(ConstantsDirectory.BRAND_IMG_NAME,list.logo)
        bundle.putString(ConstantsDirectory.PROFILE_PHOTO_NAME,photo)
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