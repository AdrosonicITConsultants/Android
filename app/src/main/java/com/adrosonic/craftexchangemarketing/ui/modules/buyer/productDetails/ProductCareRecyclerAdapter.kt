package com.adrosonic.craftexchangemarketing.ui.modules.buyer.productDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ItemProductCareListBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.ProductCare
//import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.ProductCare

class ProductCareRecyclerAdapter(var context: Context?, private var careList: List<ProductCare>) : RecyclerView.Adapter<ProductCareRecyclerAdapter.ViewHolder>(){


    inner class ViewHolder(val binding: ItemProductCareListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(care: ProductCare){
//            binding.productCare = care
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemProductCareListBinding = DataBindingUtil.inflate(inflater, R.layout.item_product_care_list,parent, false)
        return ViewHolder(binding)}

    override fun getItemCount(): Int = careList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = careList[position]
        holder.bind(product)
        var careId = product.id
        when(careId){
            1.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care1)
            }
            2.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care2)
            }
            3.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care3)
            }
            4.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care4)
            }
            5.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care5)
            }
            6.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care6)
            }
            7.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care7)
            }
            8.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care8)
            }
            9.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care9)
            }
            10.toLong() -> {
                holder.binding.careIcon.setImageResource(R.drawable.ic_care10)
            }
        }
        holder.binding.careText.text = product.productCareDesc
    }

    internal fun setProducts(care: List<ProductCare>) {
        this.careList = care
        notifyDataSetChanged()
    }

}