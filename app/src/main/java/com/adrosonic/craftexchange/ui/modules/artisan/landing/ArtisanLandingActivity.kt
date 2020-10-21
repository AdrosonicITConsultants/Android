package com.adrosonic.craftexchange.ui.modules.artisan.landing

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.ActivityArtisanLandingBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.Notification.SaveUserTokenResponse
import com.adrosonic.craftexchange.ui.modules.Notification.NotifcationFragment
import com.adrosonic.craftexchange.ui.modules.artisan.profile.artisanProfileIntent
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.CommonEnquiryFragment
import com.adrosonic.craftexchange.ui.modules.dashboard.dashboardIntent
import com.adrosonic.craftexchange.ui.modules.order.CommonOrderFragment
import com.adrosonic.craftexchange.ui.modules.role.roleselectIntent
import com.adrosonic.craftexchange.ui.modules.search.searchSuggestionIntent
import com.adrosonic.craftexchange.ui.modules.transaction.transactionIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.CMSViewModel
import com.adrosonic.craftexchange.viewModels.LandingViewModel
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_artisan_landing.*
import kotlinx.android.synthetic.main.nav_header_landing.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun Context.artisanLandingIntent(): Intent {
    return Intent(this, ArtisanLandingActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
fun Context.artisanLandingIntent(isNotification:Boolean): Intent {
    val intent = Intent(this, ArtisanLandingActivity::class.java)
    intent.putExtra("isNotification", isNotification)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    return intent
//    return Intent(this, ArtisanLandingActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    }
}
class ArtisanLandingActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ProfileViewModel.FetchUserDetailsInterface,
    LandingViewModel.notificationInterface,
    NotifcationFragment.Companion.notifcationsInterface{

    companion object{
        const val TAG = "ArtisanLanding"
    }

    private var mBinding : ActivityArtisanLandingBinding ?= null
    val mViewModel:LandingViewModel by viewModels()
//    val mCMSViewModel : CMSViewModel by viewModels()
    var craftUser : MutableLiveData<CraftUser>?= null
    val mProVM : ProfileViewModel by viewModels()
    var profileImage : String ?= ""
    var urlPro : String ?= ""
    var noti_badge:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanLandingBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mViewModel?.noficationlistener=this

        if(Utility.checkIfInternetConnected(applicationContext)){
            mViewModel?.getAllNotifications()
            mViewModel?.getMoqDeliveryTimes()
            refreshProfile()

            DeviceRegistration(object : DeviceTokenCallback {
                override fun registeredToken(token: String) {
                    addUserDevice(true,token)
                }
            }).execute()
        }else{
            Utility?.displayMessage(getString(R.string.no_internet_connection),applicationContext)
        }
        mProVM.listener = this

        mProVM.getUserMutableData()
            .observe(this, Observer<CraftUser> {
                craftUser = MutableLiveData(it)
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

        var firstname = Prefs.getString(ConstantsDirectory.FIRST_NAME,"User")

        mBinding?.navView?.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).text_user.text = firstname

        mBinding?.txtVerTag?.text=ConstantsDirectory.VERSION
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction() .replace(R.id.artisan_home_container,ArtisanHomeFragment.newInstance())
                .detach(ArtisanHomeFragment())
                .attach(ArtisanHomeFragment())
                .commitNow()

        }
        if (intent.extras != null) {
            if (intent.getBooleanExtra("isNotification", false)) {
                supportFragmentManager.beginTransaction() .add(R.id.artisan_home_container, NotifcationFragment.newInstance())
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
                                .replace(R.id.artisan_home_container,
                                    ArtisanHomeFragment.newInstance()
                                )
                                .detach(ArtisanHomeFragment())
                                .attach(ArtisanHomeFragment())
                                .commitNow()
                        }
                        return true
                    }

                    R.id.action_enquiries -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction() .add(R.id.artisan_home_container, CommonEnquiryFragment.newInstance())
                                .addToBackStack(null)
                                .commit()
                        }
                        return true
                    }

                    R.id.action_orders -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction() .add(R.id.artisan_home_container, CommonOrderFragment.newInstance())
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
                    supportFragmentManager.beginTransaction() .add(R.id.artisan_home_container, NotifcationFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
            }
        }
        setupBadge()
        return super.onOptionsItemSelected(item)
    }
    private fun setupBadge() {
        val count= UserConfig.shared?.notiBadgeCount?:0
        if (noti_badge != null) {
            if (count< 1) {
                if (noti_badge?.getVisibility() !== View.GONE) {
                    noti_badge?.setVisibility(View.GONE)
                }
            } else {
                noti_badge?.text="$count"
                if (noti_badge?.visibility !== View.VISIBLE) {
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
                startActivity(artisanProfileIntent())
            }
            R.id.nav_my_transactions -> {
                startActivity(transactionIntent())
            }
            R.id.nav_my_orders -> {}
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
                            mViewModel?.logoutUser()
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
            Log.e(TAG,"token: $token")
            return token
        }

        override fun onPostExecute(result: String) {
            callback.registeredToken(result)
        }
    }

    private fun addUserDevice(login: Boolean,authtoken:String) {
        try {
            var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
            Log.e(TAG, "authtoken: $authtoken")
            val deviceRegistration = CraftExchangeRepository.getRegisterService().saveDeviceToken(token,authtoken,authtoken)
            deviceRegistration.enqueue(object : Callback<SaveUserTokenResponse> {
                override fun onResponse(call: Call<SaveUserTokenResponse>, response: Response<SaveUserTokenResponse>?) {
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
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRestart() {
        super.onRestart()
        refreshProfile()
    }

    private fun setProfileImage(){
        profileImage = craftUser?.value?.profilePic
        urlPro = Utility.getProfilePhotoUrl(Prefs.getString(ConstantsDirectory.USER_ID, "").toLong(),profileImage)
        ImageSetter.setImageWithProgress(applicationContext,urlPro!!,nav_view.getHeaderView(0).logo,nav_view.getHeaderView(0).progress,
            R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("LandingAct", "Onsuccess")
                craftUser = mProVM.getUserMutableData()
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
                    "Error while fetching User Data. Pleas try again after some time",
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
            mViewModel?.getProductsOfArtisan(this)
            mViewModel?.getProductUploadData()
            mViewModel?.getEnquiryStageData()
//            mViewModel?.getProgressTimeData()
            mViewModel?.getInnerEnquiryStageData()
            mViewModel?.getQCStageData()
            mViewModel?.getQCQuestionData()
            mViewModel?.getEnquiryStageAvailableProdsData()
            mViewModel?.getAllNotifications()
            mProVM.getArtisanProfileDetails(this)
            mViewModel?.getTransactionStatus()
            craftUser = mProVM.getUserMutableData()
            setProfileImage()

//            mCMSViewModel?.getCategoriesData()

        }
    }

    override fun onBadgeCOuntUpdated() {
        setupBadge()
    }

    override fun onNotificationDataFetched() {
        setupBadge()
    }
}