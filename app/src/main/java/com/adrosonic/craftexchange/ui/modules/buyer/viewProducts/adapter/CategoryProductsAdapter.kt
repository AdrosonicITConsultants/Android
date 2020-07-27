package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.databinding.ItemProductDescListBinding
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.Product
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.ProductType
import com.adrosonic.craftexchange.ui.interfaces.CategoryProductClick
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import java.util.*


class CategoryProductsAdapter(var context: Context?, private var categoryProduct: List<ProductCard>) : RecyclerView.Adapter<CategoryProductsAdapter.ViewHolder>(),
    CategoryProductClick {

    inner class ViewHolder(val binding: ItemProductDescListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryProduct: ProductCard){
            binding.categoryDetails = categoryProduct
            binding.catEvent = this@CategoryProductsAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemProductDescListBinding = DataBindingUtil.inflate(inflater, R.layout.item_product_desc_list,parent, false)
        return ViewHolder(binding)    }

    override fun getItemCount(): Int = categoryProduct.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = categoryProduct[position]
        var rnd = Random()
        var currentColor = Color.argb(200, rnd.nextInt(202), rnd.nextInt(256), rnd.nextInt(256))
        holder.bind(product)
        holder.binding.productDescBck.setBackgroundColor(currentColor)
        holder.binding.productTitle.text = product.productTitle
        var status : String ?= ""
        when(product.statusId){
            1.toLong() -> {
                status = ConstantsDirectory.AVAILABLE_IN_STOCK
                holder.binding.productAvailableText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.light_green)
                }?.let { holder.binding.productAvailableText.setTextColor(it) }
            }
            2.toLong() -> {
                status = ConstantsDirectory.MADE_TO_ORDER
                holder.binding.productAvailableText.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_orange)
                }?.let { holder.binding.productAvailableText.setTextColor(it) }
            }
        }
        holder.binding.productDescription.text = product.productDescription

//        holder.binding.prodImg.setBackgroundColor(currentColor) // TODO : to be commented later
//        holder.binding.prodText.text= product.productDesc
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(categoryProduct: List<ProductCard>) {
        this.categoryProduct = categoryProduct
        notifyDataSetChanged()
    }

    override fun onItemClick(list: Product) {
//        Toast.makeText(context,"$list", Toast.LENGTH_SHORT).show()
//        var bundle = Bundle()
//        bundle.putString(ConstantsDirectory.VIEW_PROD_OF,list.productDesc)
//        var frag2 = CategoryProdListFragment()
//        frag2.arguments = bundle
//        var activity = context as BuyerLandingActivity
//        activity.supportFragmentManager.beginTransaction()
//            .replace(R.id.buyer_home_container, frag2)
//            .addToBackStack(null)
//            .commit()
    }

}