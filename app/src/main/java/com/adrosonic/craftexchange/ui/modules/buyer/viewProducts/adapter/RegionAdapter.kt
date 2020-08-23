package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.ItemRegionProductBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.buyer.viewProducts.productCatalogue.CatalogueProductsResponse
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.Cluster
import com.adrosonic.craftexchange.ui.interfaces.ClusterProductClick
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists.RegionProdListFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class RegionAdapter(var context: Context?, private var regionProducts: RealmResults<ClusterList>?) : RecyclerView.Adapter<RegionAdapter.ViewHolder>(),
    ClusterProductClick {

    inner class ViewHolder(val binding: ItemRegionProductBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(regionProducts: ClusterList){
            binding.regionProduct = regionProducts
            binding.event = this@RegionAdapter
            binding.executePendingBindings()
        }
    }

    fun updateClusterList(newList: RealmResults<ClusterList>?){
        if (newList != null) {
            this.regionProducts=newList
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRegionProductBinding = DataBindingUtil.inflate(inflater, R.layout.item_region_product,parent, false)
        return ViewHolder(binding)    }


    override fun getItemCount(): Int {
        return regionProducts?.size?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = regionProducts?.get(position)
        product?.let { holder.bind(it) }
        holder.binding.clusterProdName.text= product?.cluster
        holder.binding.clusterProdAdjective.text = product?.adjective
        //TODO : Img to be Implemented using CMS
//        product.productImageId?.let { holder.binding.prodImg.setImageResource(it) }
    }

    override fun onItemClick(list: ClusterList) {
        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        list.clusterid?.let {
            CraftExchangeRepository
                .getProductService()
                .getProductByCluster(token, it)
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
        }
        var bundle = Bundle()
        bundle.putString(ConstantsDirectory.CLUSTER_ID,list.clusterid.toString())
        bundle.putString(ConstantsDirectory.CLUSTER_PRODUCTS,list.cluster)
        var frag2 = RegionProdListFragment()
        frag2.arguments = bundle
        var activity = context as BuyerLandingActivity
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.buyer_home_container, frag2)
            .addToBackStack(null)
            .commit()
    }

}