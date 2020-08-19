package com.adrosonic.craftexchange.ui.modules.search

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.repository.data.response.search.SuggData
import com.adrosonic.craftexchange.repository.data.response.search.SuggestionResponse
import com.adrosonic.craftexchange.ui.modules.artisan.landing.ArtisanLandingActivity
import com.adrosonic.craftexchange.ui.modules.artisan.search.ArtisanSearchActivity
import com.adrosonic.craftexchange.ui.modules.artisan.search.ArtisanSearchResultsFragment
import com.adrosonic.craftexchange.utils.Utility

class SuggestionAdapter(
    var mContext: Context?,
    var list: ArrayList<SuggData>
) : RecyclerView.Adapter<SuggestionAdapter.MyViewHolder>() {


    inner class MyViewHolder(view : View): RecyclerView.ViewHolder(view) {
        var text: TextView = view.findViewById(R.id.suggestion_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestionAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int{
        return list?.size?:0
    }


    override fun onBindViewHolder(holder: SuggestionAdapter.MyViewHolder, position: Int) {
        var suggestion = list?.get(position)



        holder.text?.text = "${suggestion?.suggestion} in ${suggestion?.suggestionType}"
        holder?.itemView?.setOnClickListener {
            var activity = mContext as ArtisanSearchActivity
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.ss_container, ArtisanSearchResultsFragment.newInstance(suggestion?.suggestion))
                ?.addToBackStack(null)
                ?.commit()

        }
    }

}