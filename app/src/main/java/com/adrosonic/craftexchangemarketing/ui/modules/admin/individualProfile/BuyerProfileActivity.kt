package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityIndBuyerProfileBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.UserProfileResponse
//import com.adrosonic.craftexchangemarketing.ui.modules.buyer.profile.BuyerProfileActivity
import com.adrosonic.craftexchangemarketing.ui.modules.dashboard.OpenEnquirySummaryActivity
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.UserProfileViewModal
import com.google.gson.GsonBuilder
import com.pixplicity.easyprefs.library.Prefs

fun Context.BuyerProfileIntent(buyerId:Long): Intent {
    val intent = Intent(this, BuyerProfileActivity::class.java)
    intent.putExtra("buyerId", buyerId)
    return intent.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        Intent.FLAG_ACTIVITY_NEW_TASK  }
}

class BuyerProfileActivity : AppCompatActivity(),
UserProfileViewModal.ProfileDataInterface,
UserProfileViewModal.DeactivateInterface,
UserProfileViewModal.ActivateInterface{

    private var mBinding : ActivityIndBuyerProfileBinding?= null
    var id : Int ?= 0
    val mUPVM : UserProfileViewModal by viewModels()
    private var mUserConfig = UserConfig()
    var indUserData : String ?=""
    var userProfileResponse : UserProfileResponse?= null
    var initialData = false
    var userId : Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUPVM.profileListener = this
        mUPVM.activateListener = this
        mUPVM.deactivateListener = this
        if(intent.extras!=null){
            userId = intent.getLongExtra("buyerId", 0)
            UserConfig.shared.indUserDataJson= ""

        }
        Log.e("BuyerProfileActivity","userId: $userId")
//        userId = 10
        if(Utility.checkIfInternetConnected(applicationContext)){
//            Utility.displayMessage("calling function", applicationContext)
            mUPVM?.getUserData(userId!!)
//            Utility.displayMessage("function called", applicationContext)

        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection), applicationContext)
//            mBinding?.antaranImage?.setImageResource(R.drawable.antaran_image)
//            mBinding?.artisanImage?.setImageResource(R.drawable.artisan_catalogue_image)
        }

        mBinding = ActivityIndBuyerProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        mBinding?.menuBuyerProfileIcon?.setOnClickListener {
            mBinding?.layoutForMenuBuyer?.visibility = View.VISIBLE
            initialData = false
        }
        mBinding?.layoutForMenuBuyer?.setOnClickListener {
            mBinding?.layoutForMenuBuyer?.visibility = View.GONE
        }
        mBinding?.menuBuyerProfile?.setOnClickListener {
        }
        if(userProfileResponse?.data?.status == 1 )
        {

        }
        mBinding?.statusBuyerSwitch?.setOnCheckedChangeListener {_, isChecked ->
            if(initialData){
                initialData = false
                if (isChecked) {
                    mBinding?.statusBuyer?.text = "Active"
                    mBinding?.disabledUserText?.visibility = View.GONE
                    mBinding?.layoutForMenuBuyer?.visibility = View.GONE
//                Utility.displayMessage("Activating User..", applicationContext)

                } else {
                    mBinding?.statusBuyer?.text = "Deactive"
                    mBinding?.disabledUserText?.visibility = View.VISIBLE
                    mBinding?.layoutForMenuBuyer?.visibility = View.GONE
//                Utility.displayMessage("Deactivating User..", applicationContext)

                }

            }else {
                if (isChecked) {
                    mUPVM.activateUser(userId!!)
                    mBinding?.layoutForMenuBuyer?.visibility = View.GONE
//                Utility.displayMessage("Activating User..", applicationContext)

                } else {
                    mUPVM?.deactivateUser(userId!!)
                    mBinding?.layoutForMenuBuyer?.visibility = View.GONE
//                Utility.displayMessage("Deactivating User..", applicationContext)

                }
            }
        }

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
                mBinding?.statusBuyerSwitch?.isChecked = userProfileResponse?.data?.status == 1
                mBinding?.buyerName?.text = userProfileResponse?.data?.firstName + " " + userProfileResponse?.data?.lastName
                mBinding?.buyerDesignation?.text = userProfileResponse?.data?.designation?: "NA"
                mBinding?.buyerCompany?.text = userProfileResponse?.data?.companyDetails?.companyName ?: "NA"
                mBinding?.buyerRating?.text = "Rating : " + userProfileResponse?.data?.rating?: "NA"
                mBinding?.pocName?.text = userProfileResponse?.data?.poc?.firstName?: "NA"
                mBinding?.pocMobile?.text = userProfileResponse?.data?.poc?.contactNo?: "NA"
                mBinding?.pocEmail?.text = userProfileResponse?.data?.poc?.email?: "NA"
                mBinding?.gstNo?.text = userProfileResponse?.data?.companyDetails?.gstNo?: "NA"
                mBinding?.cinNo?.text = userProfileResponse?.data?.companyDetails?.cin?: "NA"
                mBinding?.panNo?.text = userProfileResponse?.data?.pancard?: "NA"
                mBinding?.buyerEmail1?.text = userProfileResponse?.data?.email?: "NA"
                mBinding?.primaryPhone?.text = userProfileResponse?.data?.mobile?: "NA"
                mBinding?.alternatePhone?.text = userProfileResponse?.data?.alternateMobile?: "NA"
                mBinding?.brandNameDelivery?.text = userProfileResponse?.data?.companyDetails?.companyName?: "NA"
                mBinding?.buyerDeliveryAddress?.text = userProfileResponse?.data?.deliveryAddress?.line1 + " "+ userProfileResponse?.data?.deliveryAddress?.line2 + " " + userProfileResponse?.data?.deliveryAddress?.street + " " + userProfileResponse?.data?.deliveryAddress?.city + " " + userProfileResponse?.data?.deliveryAddress?.state
                mBinding?.countryDelivery7?.text = userProfileResponse?.data?.deliveryAddress?.country?.name?: "NA"
                var image = userProfileResponse?.data?.companyDetails?.logo?: "NA"
                if(image !="NA") {
                    var url = Utility.getBrandLogoUrl(userId!!.toLong(), image)
                    ImageSetter.setImage(
                        applicationContext,
                        url!!,
                        mBinding?.brandImage!!,
                        R.drawable.buyer_logo_placeholder,
                        R.drawable.buyer_logo_placeholder,
                        R.drawable.buyer_logo_placeholder
                    )
                }



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
                mBinding?.statusBuyer?.text = "Active"
                mBinding?.disabledUserText?.visibility = View.GONE



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
                mBinding?.statusBuyer?.text = "Deactive"
                mBinding?.disabledUserText?.visibility = View.VISIBLE
//                mBinding?.layoutForMenuBuyer?.visibility = View.GONE

            }
            )
        } catch (e: Exception) {
            Log.d("Profile", "dException onFailure " + e.message)
//            Utility.displayMessage("dAPI call success exception", applicationContext)

        }
    }


}