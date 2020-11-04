package com.adrosonic.craftexchange.ui.modules.rating.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.repository.data.response.Rating.QuestionRating
import com.adrosonic.craftexchange.repository.data.response.Rating.QuestionSet
import com.adrosonic.craftexchange.repository.data.response.Rating.Questions
import com.adrosonic.craftexchange.ui.modules.order.adapter.CompletedOrderListAdapter
import hyogeun.github.com.colorratingbarlib.ColorRatingBar
import io.realm.RealmResults

class ShowBuyerRatingAdapter (var context: Context?, private var ratingsReceived: ArrayList<QuestionRating>, private var questions: ArrayList<Questions>) : RecyclerView.Adapter<ShowBuyerRatingAdapter.MyViewHolder>(){
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ratingQuestion : TextView = view.findViewById(R.id.RatingQuestions)
        var ratings : ColorRatingBar = view.findViewById(R.id.IndRating)
        var layout : ConstraintLayout = view.findViewById(R.id.ShowIndRatingLayout)
    }
//    interface ShowRating{
//        fun loadData()
//    }
//    var ShowRatingListener : ShowRating?= null

    fun updateProductList( ratingsReceived1: ArrayList<QuestionRating> ){
        if (ratingsReceived1 != null ) {
            Log.d("BuyerRating", "getItemCount update: " + ratingsReceived1?.size)

            this.ratingsReceived=ratingsReceived1
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ind_buyer_rating_show, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("BuyerRating", "onBindViewHolder: " + ratingsReceived[position])
//        Log.d("BuyerRating", "onBindViewHolder2: " + questions[position])
        if(ratingsReceived[position].responseComment !== null)
        {
            holder.layout?.visibility = View.GONE
            holder.ratingQuestion?.visibility = View.GONE
            holder.ratings?.visibility = View.GONE

        }
        else{
            var questionId = ratingsReceived[position].questionId
            var itr = questions?.iterator()
            holder.ratings?.rating = (ratingsReceived[position].response)/2
            if(itr!=null)
            {
                while (itr.hasNext())
                {
                    var data = itr.next()
                    if(data.id.toInt() == questionId )
                    {
                        holder.ratingQuestion?.text = data.question
                    }
                }

            }
        }
    }


    override fun getItemCount(): Int {
        Log.d("BuyerRating", "getItemCount: " + ratingsReceived?.size)

        return ratingsReceived?.size?:0
    }

}