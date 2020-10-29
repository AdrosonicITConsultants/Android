package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.BrandList
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
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
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class BrandAdapter(var context: Context?, private var brandList: RealmResults<BrandList>?) : RecyclerView.Adapter<BrandAdapter.ViewHolder>(),
    BrandProductClick {

    var url : String ?= ""

    inner class ViewHolder(val binding: ItemBrandProductsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(brandList: BrandList){
            binding.brandDetails = brandList
            binding.event = this@BrandAdapter
            binding.executePendingBindings()
        }
    }

    fun updateBrandList(newList: RealmResults<BrandList>?){
        if (newList != null) {
            this.brandList=newList
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemBrandProductsBinding = DataBindingUtil.inflate(inflater, R.layout.item_brand_products,parent, false)
        return ViewHolder(binding)    }


    override fun getItemCount(): Int {
        return brandList?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = brandList?.get(position)
        product?.let { holder.bind(it) }
        if(product?.logo != null){
            url = Utility.getBrandLogoUrl(product.artisanId,product.logo)
        }else{
            url = Utility.getProfilePhotoUrl(product?.artisanId,product?.profilePic)
        }

        ImageSetter.setImage(context!!,url!!,holder.binding.prodImg,R.drawable.artisan_logo_placeholder
        ,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)

        holder.binding.prodText.text = product?.companyName ?: product?.firstName
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }


    override fun onItemClick(list: BrandList) {
        var bundle = Bundle()
        bundle.putString(ConstantsDirectory.ARTISAN_ID,list.artisanId.toString())
        bundle.putString(ConstantsDirectory.ARTISAN,list.firstName)
        bundle.putString(ConstantsDirectory.COMP_NAME,list.companyName)
        bundle.putString(ConstantsDirectory.BRAND_IMG_NAME,list.logo ?: "")
        bundle.putString(ConstantsDirectory.PROFILE_PHOTO_NAME,list.profilePic ?: "")
        var frag2 = BrandProdListFragment()
        frag2.arguments = bundle
        var activity = context as BuyerLandingActivity
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.buyer_home_container, frag2)
            .addToBackStack(null)
            .commit()
    }


}