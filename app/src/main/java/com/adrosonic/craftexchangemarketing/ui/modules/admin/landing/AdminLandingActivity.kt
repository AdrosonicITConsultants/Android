package com.adrosonic.craftexchangemarketing.ui.modules.admin.landing

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityAdminLandingBinding
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.Notification.SaveUserTokenResponse
import com.adrosonic.craftexchangemarketing.ui.modules.Notification.NotifcationFragment
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.CommonUserFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_admin_landing.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun Context.adminLandingIntent(): Intent {
    return Intent(this, AdminLandingActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
//fun Context.adminLandingIntent(isNotification:Boolean): Intent {
//    val intent = Intent(this, AdminLandingActivity::class.java)
//    intent.putExtra("isNotification", isNotification)
//    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    return intent
////    return Intent(this, AdminLandingActivity::class.java).apply {
////        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////    }
//}
class AdminLandingActivity : AppCompatActivity(){

    companion object{
        const val TAG = "ArtisanLanding"
    }

    private var mBinding : ActivityAdminLandingBinding?= null
//    val mViewModel:LandingViewModel by viewModels()
//    var adminUser : MutableLiveData<CraftAdmin>?= null
//    val mProVM : ProfileViewModel by viewModels()
//    var profileImage : String ?= ""
//    var urlPro : String ?= ""
    var noti_badge: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAdminLandingBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
//        mViewModel?.noficationlistener=this
//        mViewModel?.getAllNotifications()
//        mViewModel?.getMoqDeliveryTimes()
//        ArtisanLandingActivity.DeviceRegistration(object :
//            ArtisanLandingActivity.DeviceTokenCallback {
//            override fun registeredToken(token: String) {
//                addUserDevice(true, token)
//            }
//        }).execute()
//        mProVM.listener = this
//        refreshProfile()
//        mProVM.getUserMutableData()
//            .observe(this, Observer<CraftAdmin> {
//                craftAdmin = MutableLiveData(it)
//            })
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        getSupportActionBar()?.setLogo(R.mipmap.ic_logo_main_round)
        getSupportActionBar()?.setDisplayUseLogoEnabled(true);
//        val toggle = ActionBarDrawerToggle(
//            this, drawer_layout, toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        toggle.isDrawerIndicatorEnabled = true
//        toggle.syncState()

//        var firstname = Prefs.getString(ConstantsDirectory.FIRST_NAME,"Craft")

//        mBinding?.navView?.setNavigationItemSelectedListener(this)
//        nav_view.getHeaderView(0).text_user.text = firstname

//        mBinding?.txtVerTag?.text="23-09-20 V-1.1"
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction() .replace(
                R.id.admin_home_container,
                AdminHomeFragment.newInstance())
                .detach(AdminHomeFragment())
                .attach(AdminHomeFragment())
                .commitNow()

        }
        if(Utility.checkIfInternetConnected(applicationContext)){
//            mViewModel?.getAllNotifications()
//            mViewModel?.getMoqDeliveryTimes()
//            refreshProfile()

            DeviceRegistration(object : DeviceTokenCallback {
                override fun registeredToken(token: String) {
                    addUserDevice(true,token)
                }
            }).execute()
        }else{
            Utility?.displayMessage(getString(R.string.no_internet_connection),applicationContext)
        }
//        if (intent.extras != null) {
//            if (intent.getBooleanExtra("isNotification", false)) {
//                supportFragmentManager.beginTransaction() .add(R.id.artisan_home_container, NotifcationFragment.newInstance())
//                    .addToBackStack(null)
//                    .commit()
//            }
//        }
        tab_bar.onNavigationItemSelectedListener = object: BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(@NonNull item: MenuItem):Boolean {
                when (item.itemId) {
                    R.id.action_home -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.admin_home_container,
                                    AdminHomeFragment.newInstance()
                                )
                                .detach(AdminHomeFragment())
                                .attach(AdminHomeFragment())
                                .commitNow()
                        }
                        return true
                    }

                    R.id.user_database -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction() .add(R.id.admin_home_container, CommonUserFragment.newInstance())
                                .addToBackStack(null)
                                .commit()
                        }
                        return true
                    }

                    R.id.product_catelog -> {

                        return true

                    }

                    R.id.enquiries -> {
                        //                        initTab(BranchesFragment.newInstance(), BranchesFragment.TAG)
                        return true
                    }
                    R.id.escalations -> {
                        //                        initTab(BranchesFragment.newInstance(), BranchesFragment.TAG)
                        return true
                    }

                    else -> return false
                }
            }
        }
////        NotifcationFragment.badgeCountListener=this
    }
    private fun addUserDevice(login: Boolean,authtoken:String) {
        try {
            var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
            Log.e(TAG, "authtoken: $authtoken")
            val deviceRegistration = craftexchangemarketingRepository.getRegisterService().saveDeviceToken(token,authtoken,authtoken)
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
    class DeviceRegistration(var callback: DeviceTokenCallback) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String? {
            var token = FirebaseInstanceId.getInstance().token
            while (token == null)//this is used to get Firebase token until its null so it will save you from null pointer exception
            {
                token = FirebaseInstanceId.getInstance().token
            }
            UserConfig.shared.deviceRegistrationToken = token
            Log.e(TAG, "token: $token")
            return token
        }
        override fun onPostExecute(result: String) {
            callback.registeredToken(result)
        }

    }
    interface DeviceTokenCallback {
        fun registeredToken(token: String)
    }

//        interface DeviceTokenCallback {
//            fun registeredToken(token: String)
//        }

        override fun onOptionsItemSelected(item:MenuItem):Boolean {
        // The action bar home/up action should open or close the drawer.

        when (item.itemId) {
//            android.R.id.home -> {
//                drawer_layout.openDrawer(GravityCompat.START)
//                return true
//            }
//            R.id.action_search -> {
////                startActivity(searchSuggestionIntent())
//            }
            R.id.action_notification->{
                supportFragmentManager.beginTransaction() .add(R.id.admin_home_container, NotifcationFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        }
        setupBadge()
        return super.onOptionsItemSelected(item)
    }
//
    private fun setupBadge() {
        val count = 2
        if (noti_badge != null) {
            if (count < 1) {
                if (noti_badge?.getVisibility() !== View.GONE) {
                    noti_badge?.setVisibility(View.GONE)
                }
            } else {
                noti_badge?.text = "$count"
                if (noti_badge?.getVisibility() !== View.VISIBLE) {
                    noti_badge?.visibility = View.VISIBLE
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

    override fun onBackPressed() {

            if(fragmentManager.backStackEntryCount == 0) {
                super.onBackPressed()
            }
            else {
                fragmentManager.popBackStack()
            }

    }


}