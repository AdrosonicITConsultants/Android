package com.adrosonic.craftexchange.ui.modules.rating

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.databinding.SendRatingActivityBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.rating.RatingRequest
import com.adrosonic.craftexchange.repository.data.response.Rating.*
import com.adrosonic.craftexchange.repository.data.response.artisan.editProfile.EditBankDetailsResponse
import com.adrosonic.craftexchange.repository.remote.LoginDao
import com.adrosonic.craftexchange.ui.modules.rating.adapter.SendRatingAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList

fun Context.sendRatingIntent(enquiryId :Long): Intent {
    val intent = Intent(this, SendRatingActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        Intent.FLAG_ACTIVITY_NEW_TASK  }
}
class SendRatingActivity: LocaleBaseActivity(),
SendRatingAdapter.averagerating{
    var enquiryId : Long? = 0
    var userId : Long? = 0
    private var mUserConfig = UserConfig()
    var ratingQuestionsResponse : String ?=""
    var ratingQuestions : RatingQuestionsResponse?= null
    var ratingDataResponse : String ?=""
    var buyerRating : RatingEnquiryUserResponse?= null
    var mSendRatingAdapter : SendRatingAdapter?= null
    var ratingsReceived: ArrayList<QuestionRating> = arrayListOf()
    var questions: ArrayList<Questions> = arrayListOf()
    private var mBinding : SendRatingActivityBinding?= null
    var ratingArr : Array<Double> = arrayOf()
    private var ratingDataRequest = ArrayList<RatingRequest>()

    var i :Int?=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Log.d("BuyerRating", "onViewCreated: entered on buyerrating ")

        mBinding = SendRatingActivityBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        if(intent.extras!=null){
            enquiryId = intent.getLongExtra("enquiryId", 0)
            userId = Prefs.getString(ConstantsDirectory.USER_ID, "").toLong()
            Log.d("BuyerRating", "onViewCreated: entered on buyerrating $enquiryId  $userId")
        }
        val gson = GsonBuilder().create()
        ratingQuestionsResponse = mUserConfig.ratingQuestions.toString()
        ratingQuestions = gson.fromJson(ratingQuestionsResponse, RatingQuestionsResponse::class.java)
        ratingDataResponse = mUserConfig.showBuyerRating.toString()
        buyerRating = gson.fromJson(ratingDataResponse, RatingEnquiryUserResponse::class.java)

        when(Prefs.getString(ConstantsDirectory.PROFILE, "")){
            ConstantsDirectory.ARTISAN -> {
                questions = ratingQuestions?.data?.artisanQuestions?.ratingQuestions!!
                mBinding?.ratethetext?.text = "Rate the Buyer"
//
                if(buyerRating?.data?.isArtisanRatingDone == 1 )
                {
                    mBinding?.SendRatingLayout?.visibility = View.GONE
                    mBinding?.SentRatingLayout?.visibility = View.VISIBLE
                    ratingsReceived = buyerRating?.data?.artisanRating!!
                    var size = ratingsReceived?.size?:0

                    if(ratingsReceived!![size-1].responseComment != null)
                    {
                             size -= 1
                    }
                    else{

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
                    mBinding?.ratingSent?.text = (sum.toFloat()/size.toFloat()).toString()


                }
                else{
                    mBinding?.SendRatingLayout?.visibility = View.VISIBLE
                    mBinding?.SentRatingLayout?.visibility = View.GONE
                }

            }
            ConstantsDirectory.BUYER -> {
                questions = ratingQuestions?.data?.buyerQuestions?.ratingQuestions!!
                mBinding?.ratethetext?.text = "Rate the Artisan"
                Log.d("pageshow", "buyer ")
                Log.d("pageshow", "onCreate: " +buyerRating?.data?.isBuyerRatingDone)
                if (buyerRating?.data?.isBuyerRatingDone == 1) {
                    Log.d("pageshow", "rating done ")

                    mBinding?.SendRatingLayout?.visibility = View.GONE
                    mBinding?.SentRatingLayout?.visibility = View.VISIBLE

                    ratingsReceived = buyerRating?.data?.buyerRating!!
                    var size = ratingsReceived?.size?:0

                    if(ratingsReceived!![size-1].responseComment != null)
                    {
//                        Log.d("BuyerRatingComment", "comment Exists" )
//                        mBinding?.ResponseComment?.text = ratingsReceived!![size-1].responseComment
                        size -= 1
                    }
                    else{
//                        Log.d("BuyerRatingComment", "comment not Exists" )
//                        mBinding?.ResponseComment?.visibility = View.GONE
//                        mBinding?.ResponseText?.visibility = View.GONE


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
                    mBinding?.ratingSent?.text = (sum.toFloat()/size.toFloat()).toString()

                }
                else{
                    mBinding?.SendRatingLayout?.visibility = View.VISIBLE
                    mBinding?.SentRatingLayout?.visibility = View.GONE
                }
            }
        }


        Log.d("SendRating", "onCreate: question size" + questions.size)
        mBinding?.submitRating?.setOnClickListener {
            mBinding?.submitRating?.isEnabled = false
            ratingDataRequest.clear()
            ratingArr = mSendRatingAdapter?.getRating()!!
//            Log.d("SendRating", "onCreate: get rating " + ratingArr)
            var show = false
            for(i in 0..questions.size-1)
            {
                if(ratingArr[i]==0.0)
                {
                    show = true
                }
            }
            if(show){
                Utility.displayMessage("give ratings to all questions", this)
                mBinding?.submitRating?.isEnabled = true

            }
            else{
                Utility.displayMessage("Sending Rating ", this@SendRatingActivity)
                var sum = 0.0
                for(i in 0..questions.size-1)
                {
                    sum =sum + ratingArr[i]*2
                    ratingDataRequest.add(RatingRequest(enquiryId!!,userId!!,questions[i].id,ratingArr[i]*2,""))
                }
                var average =  sum/ratingArr.size
//                ratingDataRequest.add(RatingRequest(enquiryId!!,userId!!,questions[0].id,ratingArr[0],null))
                var commentId = questions[questions.size-1].id + 1
                val message = mBinding?.message?.text
                if (message.toString() != ""){
                    ratingDataRequest.add(RatingRequest(enquiryId!!,userId!!,commentId,0.0 , message.toString() ))

                }
                Log.d("message", "onCreate array : " + ratingDataRequest)
                var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"

                CraftExchangeRepository
                    .getUserEnquiryRatingservise()
                    .sendRatings(token , ratingDataRequest)
                    .enqueue(object : Callback, retrofit2.Callback<SendRatingResponse> {
                        override fun onFailure(call: Call<SendRatingResponse>, t: Throwable) {
//                        hideProgress()
                            t.printStackTrace()
                            Log.d("ApiSEND", "n0 response: ")
                            mBinding?.submitRating?.isEnabled = true
                            Utility.displayMessage("Something went wrong ", this@SendRatingActivity)


                        }

                        override fun onResponse(
                            call: Call<SendRatingResponse>,
                            response: retrofit2.Response<SendRatingResponse>
                        ) {
                            if (response.body()?.valid == true) {
                                Utility.displayMessage("Rated successfully ", this@SendRatingActivity)
                                Log.d("ApiSEND", "API success: ")
                                mBinding?.SendRatingLayout?.visibility = View.GONE
                                mBinding?.SentRatingLayout?.visibility = View.VISIBLE
                                mBinding?.ratingSent?.text = average.toString()
//                                mBinding?.submitRating?.isEnabled = true

                            } else {
                                Log.d("ApiSEND", "API fail: ")
                                Utility.displayMessage("api fail ", this@SendRatingActivity)
                                mBinding?.submitRating?.isEnabled = true

//                                Toast.makeText(
//                                    this@SendRatingActivity,
//                                    response.body()?.errorMessage.toString(),
//                                    Toast.LENGTH_SHORT
//                                ).show()

                            }

                        }
                    })
            }
        }
        setRecyclerList()


        var size = ratingsReceived?.size?:0

        mBinding?.backArrow?.setOnClickListener {
            onBackPressed()
        }
        mBinding?.backArrow2?.setOnClickListener {
            onBackPressed()
        }

    }
    private fun setRecyclerList(){
        mBinding?.giveRating?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mSendRatingAdapter = SendRatingAdapter(this, questions )
        mBinding?.giveRating?.adapter = mSendRatingAdapter
        mSendRatingAdapter?.averageRatingListener =this

//        mEnqListAdapter?.enqListener = this  //important to set adapter first and then call listener
        Timer().schedule(object : TimerTask() {
            override fun run() {
                loadData()
            }
        }, 100)

    }
    override fun averageRatingSuccess(ar: Array<Double>) {
        Log.e("SendRating", "averageRatingSuccess: ")
        var sum = 0.0
        var size = questions.size
        for (i in 0..questions.size - 1) {
            if (sum != null) {
                sum += ar[i] * 2
            }
        }

        mBinding?.averageRating?.text = (sum.toFloat() / size.toFloat()).toString()
    }
    private fun loadData(){
        mSendRatingAdapter?.initializeArray(questions.size)
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

}