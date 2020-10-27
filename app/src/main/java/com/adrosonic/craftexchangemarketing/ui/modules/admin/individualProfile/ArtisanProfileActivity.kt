package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityIndArtisanProfileBinding
import com.adrosonic.craftexchangemarketing.databinding.ActivityIndBuyerProfileBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserProfileResponse
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.adapter.AdminDatabaseAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.dashboard.OpenEnquirySummaryActivity
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.UserProfileViewModal
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_ind_artisan_profile.view.*


fun Context.ArtisanProfileIntent(artisanId : Long): Intent {
    val intent = Intent(this , ArtisanProfileActivity::class.java)
    intent.putExtra("artisanId", artisanId)
    return intent.apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}
class ArtisanProfileActivity  : AppCompatActivity() ,
    UserProfileViewModal.ProfileDataInterface,
    UserProfileViewModal.DeactivateInterface,
    UserProfileViewModal.ActivateInterface,
UserProfileViewModal.setRatinginterface{

    val mUPVM : UserProfileViewModal by viewModels()
    private var mUserConfig = UserConfig()
    var indUserData : String ?=""
    var userProfileResponse : UserProfileResponse?= null
    var initialData = false
    var userId : Long? = 0

    private var mBinding : ActivityIndArtisanProfileBinding?= null
    var id : Int ?= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUPVM.profileListener = this
        mUPVM.activateListener = this
        mUPVM.deactivateListener = this
        mUPVM.setratingListener =this
        if(intent.extras!=null){
            userId = intent.getLongExtra("artisanId", 9)
        }
//        userId = 9
        if(Utility.checkIfInternetConnected(applicationContext)){
//            Utility.displayMessage("calling function", applicationContext)
            mUPVM?.getUserData(userId!!)
//            Utility.displayMessage("function called", applicationContext)

        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection), applicationContext)
//            mBinding?.antaranImage?.setImageResource(R.drawable.antaran_image)
//            mBinding?.artisanImage?.setImageResource(R.drawable.artisan_catalogue_image)
        }

        mBinding = ActivityIndArtisanProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        mBinding?.menuartisanProfileIcon?.setOnClickListener {
            mBinding?.layoutForMenuArtisan?.visibility = View.VISIBLE
            mBinding?.menuArtisanProfile?.visibility = View.VISIBLE
            mBinding?.giveRating?.visibility = View.GONE
        }
        mBinding?.layoutForMenuArtisan?.setOnClickListener {
            mBinding?.layoutForMenuArtisan?.visibility = View.GONE
            mBinding?.ratingBar?.setProgress(userProfileResponse?.data?.rating!!.toFloat())

        }
        mBinding?.menuArtisanProfile?.setOnClickListener {
        }
        mBinding?.editRating?.setOnClickListener {
            mBinding?.menuArtisanProfile?.visibility = View.GONE
            mBinding?.giveRating?.visibility = View.VISIBLE
        }
        mBinding?.cancelEditRating?.setOnClickListener {
            mBinding?.layoutForMenuArtisan?.visibility = View.GONE
            mBinding?.ratingBar?.setProgress(userProfileResponse?.data?.rating!!.toFloat())

        }
        mBinding?.closeEdit?.setOnClickListener {
            mBinding?.layoutForMenuArtisan?.visibility = View.GONE
            mBinding?.ratingBar?.setProgress(userProfileResponse?.data?.rating!!.toFloat())

        }
        mBinding?.saveRating?.setOnClickListener {
            mUPVM?.setRating(userId!! , mBinding?.ratingBar?.progressFloat)
            mBinding?.layoutForMenuArtisan?.visibility = View.GONE
            mBinding?.giveRating?.visibility = View.GONE


        }
        mBinding?.statusArtisanSwitch?.setOnCheckedChangeListener {_, isChecked ->
            if(initialData){
                initialData = false

            }else {

                if (isChecked) {
                    mUPVM.activateUser(userId!!)
                    mBinding?.layoutForMenuArtisan?.visibility = View.GONE
//                Utility.displayMessage("Activating User..", applicationContext)

                } else {
                    mUPVM?.deactivateUser(userId!!)
                    mBinding?.layoutForMenuArtisan?.visibility = View.GONE
//                Utility.displayMessage("Deactivating User..", applicationContext)

                }
            }

        }
//            ?.progress = userProfileResponse?.data?.rating
//        mBinding?.cancelEditRating?.setOnClickListener {
//
//        }
//        childFragmentManager.let {
//
//        }
//        tabLayout = findViewById(R.id.profileTabLayout)
//        viewPager = findViewById(R.id.artisanDetailsPager)
//        val adapter = ArtisanProfileAdapter(this, supportFragmentManager)
//        val adapter = ArtisanProfileAdapter(this,supportFragmentManager)
        mBinding?.artisanDetailsPager?.adapter = ArtisanProfileAdapter(this,supportFragmentManager)
        mBinding?.profileTabLayout?.setupWithViewPager(mBinding?.artisanDetailsPager)

    }
    override fun onProfileFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d("Profile", "OnFailure")
//                Utility.displayMessage("API call fail", applicationContext)

            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "Exception onFailure " + e.message)
//            Utility.displayMessage("API call fail exception", applicationContext)

        }
    }
    override fun onProfileSuccess() {
        try {

            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d("Profile", "OnSuccess")
//                Utility.displayMessage("API call success", applicationContext)
                indUserData = mUserConfig.indUserDataJson.toString()
                val gson = GsonBuilder().create()
                userProfileResponse = gson.fromJson(indUserData, UserProfileResponse::class.java)
                Log.d("API data ", "onProfileSuccess:" +  userProfileResponse )
                initialData= true
                 if(userProfileResponse?.data?.status == 1) {
                     mBinding?.statusArtisanSwitch?.isChecked = true
                     mBinding?.statusArtisan?.text = "Active"
                     mBinding?.disabledUserText?.visibility = View.GONE
                     mBinding?.layoutForMenuArtisan?.visibility = View.GONE
                }
                else{
                     mBinding?.statusArtisanSwitch?.isChecked = false
                     mBinding?.statusArtisan?.text = "Deactive"
                     mBinding?.disabledUserText?.visibility = View.VISIBLE
                     mBinding?.layoutForMenuArtisan?.visibility = View.GONE

                 }
                mBinding?.artisanName?.text = userProfileResponse?.data?.firstName + " " + userProfileResponse?.data?.lastName
                mBinding?.artisanWeaverId?.text = userProfileResponse?.data?.weaverId
//                mBinding?.buyerCompany?.text = userProfileResponse?.data?.companyDetails?.companyName
                mBinding?.artisanRating?.text = "Rating : " + userProfileResponse?.data?.rating
//                mBinding?.pocName?.text = userProfileResponse?.data?.poc?.firstName
//                mBinding?.pocMobile?.text = userProfileResponse?.data?.poc?.contactNo
//                mBinding?.pocEmail?.text = userProfileResponse?.data?.poc?.email
//                mBinding?.gstNo?.text = userProfileResponse?.data?.companyDetails?.gstNo
//                mBinding?.cinNo?.text = userProfileResponse?.data?.companyDetails?.cin
//                mBinding?.panNo?.text = userProfileResponse?.data?.pancard
//                mBinding?.buyerEmail1?.text = userProfileResponse?.data?.email
//                mBinding?.primaryPhone?.text = userProfileResponse?.data?.mobile
//                mBinding?.alternatePhone?.text = userProfileResponse?.data?.alternateMobile
//                mBinding?.brandNameDelivery?.text = userProfileResponse?.data?.companyDetails?.companyName
//                mBinding?.buyerDeliveryAddress?.text = userProfileResponse?.data?.deliveryAddress?.line1 + " "+ userProfileResponse?.data?.deliveryAddress?.line2 + " " + userProfileResponse?.data?.deliveryAddress?.street + " " + userProfileResponse?.data?.deliveryAddress?.city + " " + userProfileResponse?.data?.deliveryAddress?.state
//                mBinding?.countryDelivery7?.text = userProfileResponse?.data?.deliveryAddress?.country?.name
                var image = userProfileResponse?.data?.profilePic
                var url = Utility.getProfilePhotoUrl(userId!!.toLong() , image)
                ImageSetter.setImage(
                    applicationContext,
                    url!!,
                    mBinding?.profileImage!!,
                    R.drawable.buyer_logo_placeholder,
                    R.drawable.buyer_logo_placeholder,
                    R.drawable.buyer_logo_placeholder
                )
                mBinding?.ratingBar?.setProgress(userProfileResponse?.data?.rating!!.toFloat())



            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "Exception onFailure " + e.message)
//            Utility.displayMessage("API call success exception", applicationContext)

        }
    }
    override fun onActivateFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d("Profile", "aOnFailure")
                Utility.displayMessage("aAPI call fail", applicationContext)

            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "aException onFailure " + e.message)
            Utility.displayMessage("aAPI call fail exception", applicationContext)

        }
    }
    override fun onActivateSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d("Profile", "aOnSuccess")
//                Utility.displayMessage("aAPI call success", applicationContext)
                Utility.displayMessage("User Activated", applicationContext)
                mBinding?.statusArtisan?.text = "Active"
                mBinding?.disabledUserText?.visibility = View.GONE
//                mBinding?.artisanRating?.text = "Rating : " + userProfileResponse?.data?.rating
            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "aException onFailure " + e.message)
//            Utility.displayMessage("aAPI call success exception", applicationContext)

        }
    }
    override fun onDeactivateFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d("Profile", "dOnFailure")
                Utility.displayMessage("dAPI call fail", applicationContext)

            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "dException onFailure " + e.message)
            Utility.displayMessage("dAPI call fail exception", applicationContext)

        }
    }
    override fun onDeactivateSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d("Profile", "dOnSuccess")
//                Utility.displayMessage("dAPI call success", applicationContext)
                Utility.displayMessage("User Deactivated", applicationContext)
                mBinding?.statusArtisan?.text = "Deactive"
                mBinding?.disabledUserText?.visibility = View.VISIBLE
//                mBinding?.layoutForMenuBuyer?.visibility = View.GONE

            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "dException onFailure " + e.message)
//            Utility.displayMessage("dAPI call success exception", applicationContext)

        }

    }
    override fun onRatingFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d("Profile", "dOnFailure")
                Utility.displayMessage("Rating set call fail", applicationContext)

            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "dException onFailure " + e.message)
            Utility.displayMessage("dRating set fail exception", applicationContext)

        }
    }
    override fun onRatingSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.d("Profile", "dOnSuccess")
//                Utility.displayMessage("dAPI call success", applicationContext)
                Utility.displayMessage("Rating Updated", applicationContext)
                userProfileResponse?.data?.rating = mBinding?.ratingBar?.progressFloat!!
                mBinding?.artisanRating?.text = "Rating : " + userProfileResponse?.data?.rating


//                mBinding?.statusArtisan?.text = "Deactive"
//                mBinding?.disabledUserText?.visibility = View.VISIBLE

//                mBinding?.layoutForMenuBuyer?.visibility = View.GONE

            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "dException onFailure " + e.message)
//            Utility.displayMessage("dAPI call success exception", applicationContext)

        }

    }

}