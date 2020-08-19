package com.adrosonic.craftexchange.ui.modules.artisan.landing

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.databinding.ActivityArtisanLandingBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.ui.modules.artisan.profile.artisanProfileIntent
import com.adrosonic.craftexchange.ui.modules.artisan.search.searchArtisanIntent
import com.adrosonic.craftexchange.viewModels.LandingViewModel
import com.adrosonic.craftexchange.ui.modules.role.roleselectIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_artisan_landing.drawer_layout
import kotlinx.android.synthetic.main.activity_artisan_landing.nav_view
import kotlinx.android.synthetic.main.activity_artisan_landing.tab_bar
import kotlinx.android.synthetic.main.nav_header_landing.view.*
import kotlinx.android.synthetic.main.nav_header_landing.view.logo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

fun Context.artisanLandingIntent(): Intent {
    return Intent(this, ArtisanLandingActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class ArtisanLandingActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ProfileViewModel.FetchUserDetailsInterface{

    companion object{
        const val TAG = "ArtisanLanding"
    }

    private var mBinding : ActivityArtisanLandingBinding ?= null
    private var mViewModel: LandingViewModel? =null
    var craftUser : MutableLiveData<CraftUser>?= null
    val mProVM : ProfileViewModel by viewModels()
    var profileImage : String ?= ""
    var urlPro : String ?= ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanLandingBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        DeviceRegistration(object : DeviceTokenCallback {
            override fun registeredToken(token: String) {
                addUserDevice(true,token)
            }
        }).execute()



        mViewModel = ViewModelProviders.of(this).get(LandingViewModel::class.java)
        mProVM?.listener = this

        refreshProfile()
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

        var firstname = Prefs.getString(ConstantsDirectory.FIRST_NAME,"Craft")
//        var lastname = Prefs.getString(ConstantsDirectory.LAST_NAME,"User")

//        var username = "$firstname $lastname"


        mBinding?.navView?.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).text_user.text = firstname

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.artisan_home_container,
                    ArtisanHomeFragment.newInstance()
                )
                .detach(ArtisanHomeFragment())
                .attach(ArtisanHomeFragment())
                .commitNow()
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
                        //                        initTab(CalculatorFragment.newInstance(), CalculatorFragment.TAG)
                        return true
                    }

                    R.id.action_wishlist -> {
                        //                        initTab(HistoryFragment.newInstance(), HistoryFragment.TAG)
                        // showBFXProductsMenu();
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
    }
    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        // The action bar home/up action should open or close the drawer.
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_search -> {
                startActivity(searchArtisanIntent())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.tool_menu, menu)
        // Associate searchable configuration with the SearchView
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
////        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
////            setSearchableInfo(searchManager.getSearchableInfo(componentName))
////        }
//        (menu.findItem(R.id.action_search)).setOnMenuItemClickListener {
//
//        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivity(artisanProfileIntent())
            }
            R.id.nav_my_transactions -> {}
            R.id.nav_my_orders -> {}
            R.id.nav_my_dashboard -> {}
            R.id.nav_support -> {}
            R.id.nav_logout -> {
                if (Utility.checkIfInternetConnected(this)) {
                    val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                    builder.setMessage(R.string.logout_text)
                        .setPositiveButton("Yes"){ dialog, id ->
                            dialog.cancel()
                            mViewModel?.logoutUser()
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
                super.onBackPressed();
            }
            else {
                fragmentManager.popBackStack();
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
            Log.i("token",token)
            return token
        }

        override fun onPostExecute(result: String) {
            callback.registeredToken(result)
        }

    }

    private fun addUserDevice(login: Boolean,authtoken:String) {
        try {

            val deviceRegistration = CraftExchangeRepository.getRegisterService().registerToken(authtoken)

            deviceRegistration.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>?) {
                    response?.takeUnless { response.isSuccessful }?.apply {
                        Log.e(TAG, "Error registering device token "+response.message()+" raw code "+response.raw().code())
                    }
                    response?.takeIf { response.isSuccessful }?.apply {
                        Log.e(TAG, "Device registration successful")
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Error registering device token ")
//                    addUserDevice(true)n
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
            mProVM.getArtisanProfileDetails(this)
            craftUser = mProVM.getUserMutableData()
            setProfileImage()
        }
    }
}