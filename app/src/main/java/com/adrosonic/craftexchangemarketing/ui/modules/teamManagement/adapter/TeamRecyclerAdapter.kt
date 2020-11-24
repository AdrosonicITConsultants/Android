package com.adrosonic.craftexchangemarketing.ui.modules.teamManagement.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.databinding.ItemArtisanProductBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.search.SuggData
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminsData
import com.adrosonic.craftexchangemarketing.ui.modules.admin.landing.AdminLandingActivity
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.addProductIntent
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.search.ArtisanSearchResultsFragment
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.search.BuyerSearchResultFragment
import com.adrosonic.craftexchangemarketing.ui.modules.search.SearchSuggestionActivity
import com.adrosonic.craftexchangemarketing.ui.modules.teamManagement.ProfileDetailsFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults

class TeamRecyclerAdapter(var mContext: Context?, var list: List<AdminsData>) : RecyclerView.Adapter<TeamRecyclerAdapter.MyViewHolder>() {


    inner class MyViewHolder(view : View): RecyclerView.ViewHolder(view) {
        var adName : TextView = view.findViewById(R.id.admin_name)
        var adDesig : TextView = view.findViewById(R.id.admin_designation)
        var adEmail: TextView = view.findViewById(R.id.admin_email)

        var layout : LinearLayout = view.findViewById(R.id.team_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_team_recycler, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int{
        return list.size
    }

    fun updateList(mList : List<AdminsData>){
        if(mList!=null){
            this.list = mList
        }
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var details = list[position]

        holder.adName?.text = details.username
        holder.adDesig?.text = details.role
        holder.adEmail?.text = details.email

        holder?.layout?.setOnClickListener {
            var activity = mContext as AdminLandingActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.admin_home_container,ProfileDetailsFragment.newInstance(details.id,""))
                .addToBackStack(null)
                .commit()
        }
    }
}