package com.adrosonic.craftexchange.ui.modules.artisan.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.entities.realmEntities.UserAddress
import com.adrosonic.craftexchange.database.predicates.AddressPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.ActivityArtisanProfileBinding

import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.pixplicity.easyprefs.library.Prefs

fun Context.artisanProfileIntent(): Intent {
    return Intent(this, ArtisanProfileActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}

class ArtisanProfileActivity : AppCompatActivity(),
ProfileViewModel.FetchUserDetailsInterface{

    companion object{
        var craftUser = UserPredicates.findUser(Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        var regAddr = AddressPredicates.getAddressAddrType(ConstantsDirectory.REGISTERED,
            Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())

        const val TAG = "ArtisanProfileAct"
    }

    private var mBinding : ActivityArtisanProfileBinding ?= null
    val mViewModel : ProfileViewModel by viewModels()
    var craftUser : MutableLiveData<CraftUser>?= null
    var regAddr : MutableLiveData<UserAddress>?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        mViewModel.listener = this
        refreshProfile()

        mViewModel.getUserMutableData()
            .observe(this, Observer<CraftUser> {
                craftUser = MutableLiveData(it)
            })

        mViewModel.getRegAddrMutableData()
            .observe(this, Observer<UserAddress> {
                regAddr = MutableLiveData(it)
            })


        var welcome_text = "Hello ${Prefs.getString(ConstantsDirectory.FIRST_NAME,"User")}"
        mBinding?.artisanName?.text = welcome_text

        supportFragmentManager.let{
            mBinding?.viewPagerArtisanProfile?.adapter = ArtisanProfilePagerAdapter(it)
            mBinding?.tabLayoutArtisanProfile?.setupWithViewPager(mBinding?.viewPagerArtisanProfile)
        }

        mBinding?.btnBack?.setOnClickListener{
            onBackPressed()
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProfile()
    }

    private fun refreshProfile(){
        if(Utility.checkIfInternetConnected(applicationContext)) {
            mViewModel.getArtisanProfileDetails(applicationContext)
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),applicationContext)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun onSuccess() {
//        Utility?.displayMessage("Welcome!",applicationContext)
        Log.e("ArtPro","Success")
    }

    override fun onFailure() {
//        Utility?.displayMessage("Error in fetching user details!",applicationContext)
        Log.e("ArtPro","Failure")
    }
}
