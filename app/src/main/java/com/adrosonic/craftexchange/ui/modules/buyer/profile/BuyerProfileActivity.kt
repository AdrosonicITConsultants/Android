package com.adrosonic.craftexchange.ui.modules.buyer.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.adrosonic.craftexchange.database.predicates.AddressPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.ActivityBuyerProfileBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile.buyerEditProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

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
            mBinding?.viewPagerDetails?.adapter = ProfilePagerAdapter(it)
            mBinding?.tabLayoutDetails?.setupWithViewPager(mBinding?.viewPagerDetails)
        }

        mBinding?.btnEditProfile?.setOnClickListener {
            startActivity(buyerEditProfileIntent())
//            val explodeAnimation = TransitionInflater.from(this).inflateTransition(R.transition.explode)
//            explodeAnimation.duration = 1000
//            window.enterTransition = explodeAnimation
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        mBinding?.topPart?.visibility = View.VISIBLE
    }
}
