package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CategoryProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ItemCategoryProductBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.Product
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchange.ui.interfaces.CategoryProductClick
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists.CategoryProdListFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class CategoryAdapter(var context: Context?, private var categoryDetails: RealmResults<CategoryProducts>?) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>(),
    CategoryProductClick {

    inner class ViewHolder(val binding: ItemCategoryProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryDetails: CategoryProducts){
            binding.categoryDetails = categoryDetails
            binding.event = this@CategoryAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemCategoryProductBinding = DataBindingUtil.inflate(inflater, R.layout.item_category_product,parent, false)
        return ViewHolder(binding)    }

    override fun getItemCount(): Int {
        return categoryDetails?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = categoryDetails?.get(position)
//        val rnd = Random()
//        val currentColor = Color.argb(200, rnd.nextInt(202), rnd.nextInt(256), rnd.nextInt(256))
        product?.let { holder.bind(it) }

//        holder.binding.prodImg.setBackgroundColor(currentColor) // TODO : to be commented later
        holder.binding.prodText.text= product?.product
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    fun updateCategoryList(newList: RealmResults<CategoryProducts>?){
        if (newList != null) {
            this.categoryDetails=newList
        }
        this.notifyDataSetChanged()
    }

    override fun onItemClick(list: CategoryProducts) {
        var bundle = Bundle()
        bundle.putString(ConstantsDirectory.VIEW_PROD_OF,list.product)
        bundle.putString(ConstantsDirectory.PRODUCT_CATEGORY_ID,list.productCategoryid.toString())
        var frag2 = CategoryProdListFragment()
        frag2.arguments = bundle
        var activity = context as BuyerLandingActivity
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.buyer_home_container, frag2)
            .addToBackStack(null)
            .commit()
    }

}