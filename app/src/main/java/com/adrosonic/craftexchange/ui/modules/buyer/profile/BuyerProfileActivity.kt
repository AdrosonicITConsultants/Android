package com.adrosonic.craftexchange.ui.modules.buyer.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.AddressPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.ActivityBuyerProfileBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile.buyerEditProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

fun Context.buyerProfileIntent(): Intent {
    return Intent(this, BuyerProfileActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}

class BuyerProfileActivity : AppCompatActivity() {

    companion object{
        var craftUser = UserPredicates.findUser(Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        var regAddr = AddressPredicates.getAddressAddrType(ConstantsDirectory.REGISTERED,
            Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        var delAddr = AddressPredicates.getAddressAddrType(ConstantsDirectory.DELIVERY,
            Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())

        const val TAG = "BuyerProfileActivity"
    }

    private var mBinding : ActivityBuyerProfileBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBuyerProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mBinding?.textFirstname?.text = Prefs.getString(ConstantsDirectory.FIRST_NAME,"Craft")
        mBinding?.textLastname?.text = Prefs.getString(ConstantsDirectory.LAST_NAME,"User")

        supportFragmentManager.let{
            mBinding?.viewPagerDetails?.adapter = BuyerProfilePagerAdapter(it)
            mBinding?.tabLayoutDetails?.setupWithViewPager(mBinding?.viewPagerDetails)
        }

        var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
        if(Utility.checkIfInternetConnected(applicationContext)) {
            CraftExchangeRepository
                .getUserService()
                .viewMyProfile(token).enqueue(object : Callback, retrofit2.Callback<ProfileResponse> {
                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(
                        call: Call<ProfileResponse>,
                        response: Response<ProfileResponse>
                    ) {
                        if(response.body()?.valid == true){
                            UserPredicates.refreshBuyerDetails(response.body()!!)
//                            ProductPredicates.insertProductCategory(response.body())
//                            UserPredicates.insertPaymentDetails(response.body())
                            AddressPredicates.refreshUserAddress(response.body()!!)
                        }else{
                            Toast.makeText(applicationContext,response.body()?.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),applicationContext)
        }

        mBinding?.btnEditProfile?.setOnClickListener {
            startActivity(buyerEditProfileIntent())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}
