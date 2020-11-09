package com.adrosonic.craftexchange.ui.modules.buyer.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.entities.realmEntities.UserAddress
import com.adrosonic.craftexchange.database.predicates.AddressPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.ActivityBuyerProfileBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile.buyerEditProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
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

class BuyerProfileActivity : LocaleBaseActivity(),
    ProfileViewModel.FetchUserDetailsInterface {

    companion object{
        var craftUser = UserPredicates.findUser(Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        var regAddr = AddressPredicates.getAddressAddrType(ConstantsDirectory.REGISTERED,
            Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        var delAddr = AddressPredicates.getAddressAddrType(ConstantsDirectory.DELIVERY,
            Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())

        const val TAG = "BuyerProfileActivity"
    }

    private var mBinding : ActivityBuyerProfileBinding ?= null
    val mViewModel : ProfileViewModel by viewModels()
    var craftUser : CraftUser ?= null
    var regAddr : UserAddress?= null
    var delAddr : UserAddress?= null
    var image : String ?= ""
    var url : String ?= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBuyerProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mViewModel.listener = this
        refreshProfile()

        mViewModel.getUserMutableData()
            .observe(this, Observer<CraftUser> {
                craftUser = it
            })

        mViewModel.getRegAddrMutableData()
            .observe(this, Observer<UserAddress> {
                regAddr = it
            })

        mViewModel.getDelAddrMutableData()
            .observe(this, Observer<UserAddress> {
                delAddr = it
            })

        supportFragmentManager.let{
            mBinding?.viewPagerDetails?.adapter = BuyerProfilePagerAdapter(it)
            mBinding?.tabLayoutDetails?.setupWithViewPager(mBinding?.viewPagerDetails)
        }

        mBinding?.btnEditProfile?.setOnClickListener {
            startActivity(buyerEditProfileIntent())
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    fun setDetails(){
        var rating = "${craftUser?.rating} / 10"
        mBinding?.rating?.text = rating

        mBinding?.textFirstname?.text = Prefs.getString(ConstantsDirectory.FIRST_NAME,"Craft")
        mBinding?.textLastname?.text = Prefs.getString(ConstantsDirectory.LAST_NAME,"User")

        setImage()
    }

    override fun onSuccess() {
//        Utility?.displayMessage("Welcome!",applicationContext)
        Log.e("BuyPro","Success")
        setDetails()
    }

    override fun onFailure() {
        Log.e("BuyPro","Failure")
    }

    override fun onResume() {
        super.onResume()
        mViewModel.getUserMutableData()
        setDetails()
    }

    fun refreshProfile(){
        if(Utility.checkIfInternetConnected(applicationContext)){
            mViewModel.getBuyerProfileDetails(this)
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),applicationContext)
        }
    }

    fun setImage() {
        image = craftUser?.brandLogo
        url = Utility.getBrandLogoUrl(Prefs.getString(ConstantsDirectory.USER_ID, "").toLong(), image)
        ImageSetter.setImage(
            applicationContext,
            url!!,
            mBinding?.logo!!,
            R.drawable.buyer_logo_placeholder,
            R.drawable.buyer_logo_placeholder,
            R.drawable.buyer_logo_placeholder
        )
    }
}
