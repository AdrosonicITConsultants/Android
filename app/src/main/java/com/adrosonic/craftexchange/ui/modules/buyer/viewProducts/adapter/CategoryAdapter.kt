package com.adrosonic.craftexchange.ui.modules.viewProducts.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ItemCategoryProductBinding
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.Product
import com.adrosonic.craftexchange.ui.interfaces.CategoryProductClick
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists.CategoryProdListFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory

class CategoryAdapter(var context: Context?, private var categoryDetails: List<Product>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>(),
    CategoryProductClick {

    inner class ViewHolder(val binding: ItemCategoryProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryDetails: Product){
            binding.categoryDetails = categoryDetails
            binding.event = this@CategoryAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemCategoryProductBinding = DataBindingUtil.inflate(inflater, R.layout.item_category_product,parent, false)
        return ViewHolder(binding)    }

    override fun getItemCount(): Int = categoryDetails.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = categoryDetails[position]
//        val rnd = Random()
//        val currentColor = Color.argb(200, rnd.nextInt(202), rnd.nextInt(256), rnd.nextInt(256))
        holder.bind(product)

//        holder.binding.prodImg.setBackgroundColor(currentColor) // TODO : to be commented later
        holder.binding.prodText.text= product.productDesc
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(categoryProduct: List<Product>) {
        this.categoryDetails = categoryProduct
        notifyDataSetChanged()
    }



    override fun onItemClick(list: Product) {
//        Toast.makeText(context,"$list",Toast.LENGTH_SHORT).show()
        var bundle = Bundle()
        bundle.putString(ConstantsDirectory.VIEW_PROD_OF,list.productDesc)
        var frag2 = CategoryProdListFragment()
        frag2.arguments = bundle
        var activity = context as BuyerLandingActivity
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.buyer_home_container, frag2)
            .addToBackStack(null)
            .commit()
    }

}