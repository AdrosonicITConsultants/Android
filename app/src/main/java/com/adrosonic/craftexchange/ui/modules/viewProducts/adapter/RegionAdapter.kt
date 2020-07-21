package com.adrosonic.craftexchange.ui.modules.viewProducts.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ItemRegionProductBinding
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.Cluster
import com.adrosonic.craftexchange.ui.interfaces.ClusterProductClick

class RegionAdapter(var context: Context?, private var regionProducts: List<Cluster>) : RecyclerView.Adapter<RegionAdapter.ViewHolder>(),
    ClusterProductClick {

    inner class ViewHolder(val binding: ItemRegionProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(regionProducts: Cluster){
            binding.regionProduct = regionProducts
            binding.event = this@RegionAdapter
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRegionProductBinding = DataBindingUtil.inflate(inflater, R.layout.item_region_product,parent, false)
        return ViewHolder(binding)    }

    override fun getItemCount(): Int = regionProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = regionProducts[position]
        holder.bind(product)
        holder.binding.clusterProdName.text= product.desc
        holder.binding.clusterProdAdjective.text = product.adjective
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    internal fun setProducts(regionProducts: List<Cluster>) {
        this.regionProducts = regionProducts
        notifyDataSetChanged()
    }

    override fun onItemClick(list: Cluster) {
        TODO("Not yet implemented")
    }


}