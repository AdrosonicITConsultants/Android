package com.adrosonic.craftexchange.ui.modules.buyer.search

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import io.realm.RealmResults

class BuyerSearchAdapter(realmResults : RealmResults<ProductCatalogue>): BaseAdapter() {

    private var products=realmResults
    private class ViewHolder {
        internal var name: TextView? = null
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun getItem(position: Int): ProductCatalogue? {
        return products[position]
    }

    override fun getItemId(position: Int): Long {
        return products[position]?._id?.toLong()!!
    }

    override fun getCount(): Int {
        return products.size
    }
}