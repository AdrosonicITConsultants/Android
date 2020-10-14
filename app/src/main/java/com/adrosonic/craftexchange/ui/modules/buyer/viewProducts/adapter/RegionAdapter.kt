package com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.databinding.ItemRegionProductBinding
import com.adrosonic.craftexchange.ui.interfaces.ClusterProductClick
import com.adrosonic.craftexchange.ui.modules.buyer.landing.BuyerLandingActivity
import com.adrosonic.craftexchange.ui.modules.buyer.viewProducts.productlists.RegionProdListFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import io.realm.RealmResults
import org.json.JSONArray

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

        if(Utility.checkIfInternetConnected(context!!)) {
            if (UserConfig.shared.regionCMS != null) {
                val dataJson = JSONArray(UserConfig.shared.regionCMS)
                Log.i("CMS", "DataJson : $dataJson")
                for (i in 0 until dataJson.length()) {
                    val dataObj = dataJson.getJSONObject(i)
                    Log.i("CMS", "DataObj : $dataObj")
                    var acfObj = dataObj?.getJSONObject("acf")
                    var clusterId = acfObj?.getString("cluster_id")?.toLong()

                    if (product?.clusterid == clusterId) {
                        holder.binding.clusterProdAdjective.text = acfObj?.getString("header")
                        var imgUrl = acfObj?.getString("image")
                        context?.let {
                            imgUrl?.let { it1 ->
                                ImageSetter.setCMSImage(
                                    it,
                                    it1, holder.binding.clusterProdImg
                                )
                            }
                        }
                    }

                }
            }
        }else{
            holder.binding.clusterProdImg.setImageResource(R.drawable.demo2_img)
        }
    }

    override fun onItemClick(list: ClusterList) {
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