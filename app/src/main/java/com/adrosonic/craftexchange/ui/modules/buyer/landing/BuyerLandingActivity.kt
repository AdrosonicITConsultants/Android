package com.adrosonic.craftexchange.ui.modules.buyer.landing

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.predicates.NotificationPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.ActivityBuyerLandingBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.Notification.NotificationReadResponse
import com.adrosonic.craftexchange.repository.data.response.Notification.SaveUserTokenResponse
import com.adrosonic.craftexchange.repository.data.response.enquiry.EnquiryProductResponse
import com.adrosonic.craftexchange.ui.modules.Notification.NotifcationFragment
import com.adrosonic.craftexchange.ui.modules.artisan.landing.ArtisanLandingActivity
import com.adrosonic.craftexchange.ui.modules.artisan.landing.artisanLandingIntent
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.CommonEnquiryFragment
import com.adrosonic.craftexchange.ui.modules.buyer.ownDesign.OwnProductListFragment
import com.adrosonic.craftexchange.ui.modules.buyer.profile.buyerProfileIntent
import com.adrosonic.craftexchange.ui.modules.buyer.wishList.wishlistFragment
import com.adrosonic.craftexchange.ui.modules.dashboard.dashboardIntent
import com.adrosonic.craftexchange.ui.modules.order.CommonOrderFragment
import com.adrosonic.craftexchange.ui.modules.role.roleselectIntent
import com.adrosonic.craftexchange.ui.modules.search.searchSuggestionIntent
import com.adrosonic.craftexchange.ui.modules.transaction.transactionIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.LandingViewModel
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_buyer_landing.*
import kotlinx.android.synthetic.main.custom_bell_icon_layout.*
import kotlinx.android.synthetic.main.nav_header_landing.view.*
import retrofit2.Call
import retrofit2.Callback

fun Context.buyerLandingIntent(): Intent {
    return Intent(this, BuyerLandingActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
}
fun Context.buyerLandingIntent(isNotification:Boolean): Intent {
    val intent = Intent(this, BuyerLandingActivity::class.java)
    intent.putExtra("isNotification", isNotification)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    return intent
//    return Intent(this, BuyerLandingActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//    }
}
class BuyerLandingActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ProfileViewModel.FetchUserDetailsInterface,
    LandingViewModel.notificationInterface,
    NotifcationFragment.Companion.notifcationsInterface{


    companion object{
        const val TAG = "buyerLanding"
    }

    private var mBinding : ActivityBuyerLandingBinding ?= null
    val mViewModel: LandingViewModel by viewModels()
    var craftUser : CraftUser?= null
    val mProVM : ProfileViewModel by viewModels()
    var imageName : String ?= ""
    var url : String ?= ""
    var noti_badge:TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityBuyerLandingBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        DeviceRegistration(object : DeviceTokenCallback {
            override fun registeredToken(token: String) {
                    addUserDevice(true,token)
            }
        }).execute()

        mViewModel.getProductUploadData()
        mViewModel.getEnquiryStageData()
        mViewModel?.getInnerEnquiryStageData()
        mViewModel.getEnquiryStageAvailableProdsData()
        mViewModel.getwishlisteProductIds()
        mViewModel?.getMoqDeliveryTimes()

        refreshProfile()
        mProVM.listener = this
        mViewModel?.noficationlistener=this


        mProVM.getUserMutableData()
            .observe(this, Observer<CraftUser> {
                craftUser = it
            })


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        var firstname = Prefs.getString(ConstantsDirectory.FIRST_NAME,"Craft")
//        var lastname = Prefs.getString(ConstantsDirectory.LAST_NAME,"User")
//
//        var username = "$firstname $lastname""

        mBinding?.navView?.setNavigationItemSelectedListener(this)
//        mBinding?.navView?.menu?.getItem(0)?.isChecked = false
        nav_view.getHeaderView(0).text_user.text = firstname
        mBinding?.txtVerTag?.text=ConstantsDirectory.VERSION
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.buyer_home_container,
                    BuyerHomeFragment.newInstance()
                )
                .detach(BuyerHomeFragment())
                .attach(BuyerHomeFragment())
                .commitNow()
        }
        if (intent.extras != null) {
            if (intent.getBooleanExtra("isNotification", false)) {
                supportFragmentManager.beginTransaction() .add(R.id.buyer_home_container, NotifcationFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        }
        tab_bar.onNavigationItemSelectedListener = object: BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(@NonNull item:MenuItem):Boolean {
                when (item.itemId) {
                    R.id.action_home -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction()
                                .add(R.id.buyer_home_container, BuyerHomeFragment.newInstance())
                                .detach(BuyerHomeFragment())
                                .attach(BuyerHomeFragment())
                                .commitNow()
                        }
                        return true
                    }

                    R.id.action_enquiries -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction() .add(R.id.buyer_home_container, CommonEnquiryFragment.newInstance())
                                .addToBackStack(null)
                                .commit()
                        }
                        return true
                    }
                    R.id.action_wishlist -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction() .add(R.id.buyer_home_container, wishlistFragment.newInstance())
                                .addToBackStack(null)
                                .commit()
                        }
                        return true
                    }
                    R.id.action_chat -> {
    //                        initTab(BranchesFragment.newInstance(), BranchesFragment.TAG)
                        return true
                    }

                    else -> return false
                }
            }
        }
        NotifcationFragment.badgeCountListener=this
//        val fab: FloatingActionButton = findViewById(R.id.fab)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

    }

    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        // The action bar home/up action should open or close the drawer.

        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }

            R.id.action_search -> {
                startActivity(searchSuggestionIntent())
            }
            R.id.action_notification->{
                supportFragmentManager.beginTransaction() .add(R.id.buyer_home_container, NotifcationFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
                return true
            }
        }
        setupBadge()
        return super.onOptionsItemSelected(item)
    }
    private fun setupBadge() {
        val count= NotificationPredicates.getAllNotifications()?.size?:0
        if (noti_badge != null) {
            if (count == 0) {
                if (noti_badge?.getVisibility() !== View.GONE) {
                    noti_badge?.setVisibility(View.GONE)
                }
            } else {
                noti_badge?.text="$count"
                if (noti_badge?.getVisibility() !== View.VISIBLE) {
                    noti_badge?.visibility=View.VISIBLE
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tool_menu, menu)
        val menuItem = menu.findItem(R.id.action_notification)
        val actionView = menuItem.actionView
        noti_badge = actionView.findViewById<View>(R.id.noti_badge) as TextView
        setupBadge()
        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivity(buyerProfileIntent())
            }
            R.id.nav_my_transactions -> {
                startActivity(transactionIntent())
            }
            R.id.nav_my_orders -> {
                supportFragmentManager.beginTransaction() .add(R.id.buyer_home_container, CommonOrderFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_custom_design -> {
            supportFragmentManager.beginTransaction() .add(R.id.buyer_home_container,OwnProductListFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
            }
            R.id.nav_my_dashboard -> {
                startActivity(dashboardIntent())
            }
            R.id.nav_support -> {}
            R.id.nav_logout -> {
                if (Utility.checkIfInternetConnected(this)) {
                    val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                    builder.setMessage(R.string.logout_text)
                        .setPositiveButton("Yes"){ dialog, id ->
                            dialog.cancel()
                            mViewModel.logoutUser()
                            UserPredicates.deleteData()
                            Utility.deleteCache(applicationContext)
                            Utility.deleteImageCache(applicationContext)
                            startActivity(roleselectIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        }
                        .setNegativeButton("No"){ dialog, id ->
                            dialog.cancel()
                        }
                    builder.create().show()

                }else{
                    Utility.displayMessage(getString(R.string.message_operation_not_supported_offline),applicationContext)
                }
            }
            else -> {
                supportActionBar?.title = ""
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            if(fragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
            }
            else {
                fragmentManager.popBackStack()
            }
        }
    }


    interface DeviceTokenCallback {
        fun registeredToken(token: String)
    }

    class DeviceRegistration(var callback: DeviceTokenCallback) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String? {
            var token = FirebaseInstanceId.getInstance().token
            while (token == null)//this is used to get Firebase token until its null so it will save you from null pointer exception
            {
                token = FirebaseInstanceId.getInstance().token
            }
            UserConfig.shared.deviceRegistrationToken = token
            Log.i("token",token)
            return token
        }

        override fun onPostExecute(result: String) {
            callback.registeredToken(result)
        }

    }

    private fun addUserDevice(login: Boolean,authtoken:String) {
        try {
            var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
            val deviceRegistration = CraftExchangeRepository.getRegisterService().saveDeviceToken(token,authtoken,authtoken)

            deviceRegistration.enqueue(object : Callback<SaveUserTokenResponse> {
                override fun onResponse(call: Call<SaveUserTokenResponse>, response: retrofit2.Response<SaveUserTokenResponse>?) {
                    response?.takeUnless { response.isSuccessful }?.apply {
                        Log.e(TAG, "Error registering device token "+response.message()+" raw code "+ response.raw().code)
                    }
                    response?.takeIf { response.isSuccessful }?.apply {
                        Log.e(TAG, "Device registration successful ${response.body()?.data?.deviceType}")
                        Log.e(TAG, "valid ${response.body()?.valid}")
                    }
                }
                override fun onFailure(call: Call<SaveUserTokenResponse>, t: Throwable) {
                    Log.e(TAG, "Error registering device token ")
//                    addUserDevice(true)n
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("LandingAct", "Onsuccess")
                mProVM.getUserMutableData()
            }
            )
        } catch (e: Exception) {
            Log.e("LandingAct", "Exception onSuccess " + e.message)
        }    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("LandingAct", "OnFailure")
                Utility.displayMessage(
                    "Error while fetching wishlist. Pleas try again after some time",
                    this
                )
            }
            )
        } catch (e: Exception) {
            Log.e("LandingAct", "Exception onFailure " + e.message)
        }
    }

    fun refreshProfile(){
        if (!Utility.checkIfInternetConnected(this)) {
            Utility.displayMessage(getString(R.string.no_internet_connection), this)
        } else {
            mViewModel.getProductsOfArtisan(this)
            mViewModel.getProductUploadData()
            mViewModel.getEnquiryStageData()
            mViewModel?.getInnerEnquiryStageData()
            mViewModel.getEnquiryStageAvailableProdsData()
            mViewModel.getArtisanBrandDetails()
            mViewModel.getAllNotifications()
            mProVM.getBuyerProfileDetails(this)
            mProVM.getUserMutableData()
            mViewModel?.getTransactionStatus()
            mViewModel?.getChangeRequestStatuses()
        }
    }

    private fun setBrandLogo(){
        imageName = craftUser?.brandLogo
        url = Utility.getBrandLogoUrl(Prefs.getString(ConstantsDirectory.USER_ID, "").toLong(),imageName)
        ImageSetter.setImageWithProgress(applicationContext,url!!,nav_view.getHeaderView(0).logo,nav_view.getHeaderView(0).progress,
            R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
    }

    override fun onRestart() {
        super.onRestart()
        refreshProfile()
        setBrandLogo()
    }

    override fun onResume() {
        super.onResume()
        mProVM.getUserMutableData()
        setBrandLogo()
    }
    override fun onBadgeCOuntUpdated() {
        setupBadge()
    }
    override fun onNotificationDataFetched() {
        setupBadge()
    }
}







