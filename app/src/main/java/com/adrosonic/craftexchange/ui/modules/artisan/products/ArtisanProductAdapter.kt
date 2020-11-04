package com.adrosonic.craftexchange.ui.modules.artisan.products

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.databinding.ItemArtisanProductCategoryBinding
import com.adrosonic.craftexchange.ui.interfaces.ArtisanProductClick
import com.adrosonic.craftexchange.ui.modules.artisan.landing.ArtisanLandingActivity
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists.RegionProdListFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import io.realm.RealmResults
import org.json.JSONArray

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

        if(Utility.checkIfInternetConnected(context!!)){
            Log.i("CMS", "DataJson : ${UserConfig.shared.categoryCMS}")
            if(UserConfig.shared.categoryCMS != null) {
                if(UserConfig.shared.categoryCMS!!.length>0) {
                    val dataJson = JSONArray(UserConfig.shared.categoryCMS)
                    Log.i("CMS", "DataJson : ${dataJson.length()}")
                    if (dataJson.length() > 0) {
                        for (i in 0 until dataJson.length()) {
                            val dataObj = dataJson.getJSONObject(i)
                            Log.i("CMS", "DataObj : $dataObj")
                            var acfObj = dataObj?.getJSONObject("acf")
                            var prodCatId = acfObj?.getString("category_id")?.toLong()

                            if (product?.productCategoryId == prodCatId) {
                                var imgUrl = acfObj?.getString("image")
                                context?.let {
                                    imgUrl?.let { it1 ->
                                        ImageSetter.setCMSImage(
                                            it,
                                            it1, holder.binding.prodImg
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            holder.binding.prodImg.setImageResource(R.drawable.demo_img)
        }


    }


    override fun onItemClick(list: ArtisanProducts) {
        var bundle = Bundle()
        list.artisanId?.let { bundle.putLong(ConstantsDirectory.ARTISAN_ID, it) }
        bundle.putString(ConstantsDirectory.PRODUCT_CATEGORY,list.productCategoryDesc)
        list.productCategoryId?.let { bundle.putLong(ConstantsDirectory.PRODUCT_CATEGORY_ID, it) }
        var frag2 = ArtisanUploadedProductsFragment()
        frag2.arguments = bundle
        var activity = context as ArtisanLandingActivity
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.artisan_home_container, frag2)
            .addToBackStack(null)
            .commit()
    }

}

