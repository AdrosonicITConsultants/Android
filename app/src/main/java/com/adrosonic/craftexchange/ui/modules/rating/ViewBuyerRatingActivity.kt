package com.adrosonic.craftexchange.ui.modules.rating

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.databinding.ActivityShowBuyerRatingBinding
import com.adrosonic.craftexchange.repository.data.response.Rating.QuestionRating
import com.adrosonic.craftexchange.repository.data.response.Rating.Questions
import com.adrosonic.craftexchange.repository.data.response.Rating.RatingEnquiryUserResponse
import com.adrosonic.craftexchange.repository.data.response.Rating.RatingQuestionsResponse
import com.adrosonic.craftexchange.ui.modules.order.adapter.CompletedOrderListAdapter
import com.adrosonic.craftexchange.ui.modules.rating.adapter.ShowBuyerRatingAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import java.util.*
import kotlin.collections.ArrayList


fun Context.viewBuyerRatingIntent(enquiryId :Long): Intent {
    val intent = Intent(this, ViewBuyerRatingActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        Intent.FLAG_ACTIVITY_NEW_TASK  }
}

class ViewBuyerRatingActivity : LocaleBaseActivity()
{

    var enquiryId : Long? = 0
    var userId : Long? = 0
    private var mUserConfig = UserConfig()
    var ratingQuestionsResponse : String ?=""
    var ratingQuestions : RatingQuestionsResponse?= null
    var ratingDataResponse : String ?=""
    var buyerRating : RatingEnquiryUserResponse?= null
    var mShowBuyerRatingAdapter : ShowBuyerRatingAdapter?= null
    var ratingsReceived: ArrayList<QuestionRating> = arrayListOf()
    var questions: ArrayList<Questions> = arrayListOf()
    private var mBinding : ActivityShowBuyerRatingBinding?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityShowBuyerRatingBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        if(intent.extras!=null){
            enquiryId = intent.getLongExtra("enquiryId", 0)
            userId = Prefs.getString(ConstantsDirectory.USER_ID, "").toLong()
            Log.d("BuyerRating", "onViewCreated: entered on buyerrating $enquiryId  $userId")
        }
//        initializeData()

        val gson = GsonBuilder().create()
        ratingQuestionsResponse = mUserConfig.ratingQuestions.toString()
        ratingQuestions = gson.fromJson(ratingQuestionsResponse, RatingQuestionsResponse::class.java)
        questions = ratingQuestions?.data?.buyerQuestions?.ratingQuestions!!

        setRecyclerList()

                ratingDataResponse = mUserConfig.showBuyerRating.toString()
                buyerRating = gson.fromJson(ratingDataResponse, RatingEnquiryUserResponse::class.java)
                ratingsReceived = buyerRating?.data?.buyerRating!!
                Log.d("BuyerRating", "onViewCreated: entered on buyerrating "+ buyerRating?.data?.buyerRating!! )
//                ratingsReceived = buyerRating?.data?.buyerRating!!
                var size = ratingsReceived?.size?:0

                if(ratingsReceived!![size-1].responseComment != null)
                {
                    Log.d("BuyerRatingComment", "comment Exists" )
                    mBinding?.ResponseComment?.text = ratingsReceived!![size-1].responseComment
                    size -= 1
                }
                else{
                    Log.d("BuyerRatingComment", "comment not Exists" )
                    mBinding?.ResponseComment?.visibility = View.GONE
                    mBinding?.ResponseText?.visibility = View.GONE


                }
                var sum = 0
                var itr = ratingsReceived?.iterator()
                if (itr!=null)
                {
                    while (itr.hasNext())
                    {
                        var data = itr.next()
                        sum += data.response.toInt()

                    }

                }
                mBinding?.averageRating?.setOnClickListener {

                }
                mBinding?.backarrow?.setOnClickListener {
                    onBackPressed()
                }

                mBinding?.averageRating?.text = (sum.toFloat()/size.toFloat()).toString()

        loadData()
    }

    private fun setRecyclerList(){
        mBinding?.RatingRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mShowBuyerRatingAdapter = ShowBuyerRatingAdapter(this,ratingsReceived, questions )
        mBinding?.RatingRecyclerView?.adapter = mShowBuyerRatingAdapter
//

    }
    private fun loadData(){

        mShowBuyerRatingAdapter?.updateProductList(ratingsReceived)


    }
    override fun onBackPressed() {
        super.onBackPressed()
    }
}