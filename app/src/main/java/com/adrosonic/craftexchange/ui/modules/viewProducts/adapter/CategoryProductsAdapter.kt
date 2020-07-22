package com.adrosonic.craftexchange.ui.modules.viewProducts.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ItemProductDescListBinding
import com.adrosonic.craftexchange.repository.data.response.viewProducts.Product
import com.adrosonic.craftexchange.repository.data.response.viewProducts.ProductType
import com.adrosonic.craftexchange.ui.interfaces.CategoryProductClick
import java.util.*


class CategoryProductsAdapter(var context: Context?, private var categoryProduct: List<ProductType>) : RecyclerView.Adapter<CategoryProductsAdapter.ViewHolder>(),
    CategoryProductClick {

    inner class ViewHolder(val binding: ItemProductDescListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryProduct: ProductType){
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
        holder.binding.productTitle.text = product.productDesc
//        holder.binding.prodImg.setBackgroundColor(currentColor) // TODO : to be commented later
//        holder.binding.prodText.text= product.productDesc
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(categoryProduct: List<ProductType>) {
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