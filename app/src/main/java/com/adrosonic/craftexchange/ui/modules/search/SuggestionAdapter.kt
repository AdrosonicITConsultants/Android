package com.adrosonic.craftexchange.ui.modules.search

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.repository.data.response.search.SuggData
import com.adrosonic.craftexchange.ui.modules.artisan.search.ArtisanSearchResultsFragment

class SuggestionAdapter(
    var mContext: Context?,
    var list: ArrayList<SuggData>
) : RecyclerView.Adapter<SuggestionAdapter.MyViewHolder>() {


    inner class MyViewHolder(view : View): RecyclerView.ViewHolder(view) {
        var sugItem: TextView = view.findViewById(R.id.suggestion_item)
    }
    var suggestionItem : String ?= ""
    var suggestionType : String ?=""


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int{
        return list.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var suggestion = list.get(position)

        suggestionItem = suggestion.suggestion
        suggestionType = suggestion.suggestionType

        var text = SpannableString(suggestionType)
        text.setSpan(ForegroundColorSpan(Color.BLACK), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.sugItem.text = "$suggestionItem in "
        holder.sugItem.append(text)

        holder.itemView.setOnClickListener {
            var activity = mContext as SearchSuggestionActivity
            activity.supportFragmentManager.beginTransaction()
                ?.replace(R.id.ss_container, ArtisanSearchResultsFragment.newInstance(suggestion?.suggestion))
                ?.addToBackStack(null)
                .commit()
        }
    }

}