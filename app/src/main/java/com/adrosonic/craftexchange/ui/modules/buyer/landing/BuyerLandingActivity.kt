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
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.predicates.NotificationPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.ActivityBuyerLandingBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.Notification.SaveUserTokenResponse
import com.adrosonic.craftexchange.ui.modules.Notification.NotifcationFragment
import com.adrosonic.craftexchange.ui.modules.artisan.landing.ArtisanHomeFragment
import com.adrosonic.craftexchange.ui.modules.pdfViewer.PdfViewerActivity
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.CommonEnquiryFragment
import com.adrosonic.craftexchange.ui.modules.buyer.ownDesign.OwnProductListFragment
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.catalogueProductDetailsIntent
import com.adrosonic.craftexchange.ui.modules.buyer.profile.buyerProfileIntent
import com.adrosonic.craftexchange.ui.modules.buyer.wishList.wishlistFragment
import com.adrosonic.craftexchange.ui.modules.chat.ChatListFragment
import com.adrosonic.craftexchange.ui.modules.dashboard.dashboardIntent
import com.adrosonic.craftexchange.ui.modules.order.CommonOrderFragment
import com.adrosonic.craftexchange.ui.modules.role.roleselectIntent
import com.adrosonic.craftexchange.ui.modules.search.searchSuggestionIntent
import com.adrosonic.craftexchange.ui.modules.transaction.transactionIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_buyer_landing.*
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
}
fun Context.buyerLandingIntentForGenEnq(isGenEnq:Boolean): Intent {
    val intent = Intent(this, BuyerLandingActivity::class.java)
    intent.putExtra("isGenEnq", isGenEnq)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    return intent
}
class BuyerLandingActivity : LocaleBaseActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ProfileViewModel.FetchUserDetailsInterface,
    LandingViewModel.notificationInterface,
    NotifcationFragment.Companion.notifcationsInterface{

    fun AppCompatActivity.replaceContainerFragment(fragment: Fragment, name:String) {
        supportFragmentManager.beginTransaction().replace(R.id.buyer_home_container, fragment, name).addToBackStack(name).commit()
    }
    companion object{
        const val TAG = "buyerLanding"
    }

    private var mBinding : ActivityBuyerLandingBinding ?= null
    val mViewModel: LandingViewModel by viewModels()
    var craftUser : CraftUser?= null
    val mProVM : ProfileViewModel by viewModels()
    val mCMSViewModel : CMSViewModel by viewModels()
    var imageName : String ?= ""
    var url : String ?= ""
    var noti_badge:TextView? = null
    val mEnqVm: EnquiryViewModel by viewModels()
    val mOrderVm: OrdersViewModel by viewModels()
    val mUserConfig=UserConfig()
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

        refreshProfile()
        mProVM.listener = this
        mViewModel?.noficationlistener=this

        if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)){
            mProVM.getUserMutableData() .observe(this, Observer<CraftUser> {
                craftUser = it
            })
        }else mBinding?.navView?.menu?.get(7)?.setTitle(R.string.switch_to_artisan)
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

        mBinding?.navView?.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).text_user.text = if(firstname.isNullOrEmpty())"Guest" else firstname

        mBinding?.txtVerTag?.text=ConstantsDirectory.VERSION
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.buyer_home_container, BuyerHomeFragment.newInstance())
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
            if (intent.getBooleanExtra("isGenEnq", false)){
                val intent = Intent(catalogueProductDetailsIntent())
                val bundle = Bundle()
                Log.e("CatalogueProductDetails", "44444 productId: ${mUserConfig.productId?.toString()}")
                bundle.putString(ConstantsDirectory.PRODUCT_ID, mUserConfig.productId?.toString())
                bundle.putBoolean("isGenEnq", true)
                intent.putExtras(bundle)
                startActivity(intent)
                mUserConfig.productId=0
                mUserConfig.isEnquiryAction=false

            }
        }
        tab_bar.onNavigationItemSelectedListener = object: BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(@NonNull item:MenuItem):Boolean {
                when (item.itemId) {
                    R.id.action_home -> {
                        if (savedInstanceState == null) {
                            replaceContainerFragment(BuyerHomeFragment.newInstance(),"BuyerHomeFragment")
                        }
                        return true
                    }

                    R.id.action_enquiries -> {
                        if (savedInstanceState == null) {
                        if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)) replaceContainerFragment(CommonEnquiryFragment.newInstance(),"CommonEnquiryFragment")
                        else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)
                        }
                        return true
                    }
                    R.id.action_wishlist -> {
                        if (savedInstanceState == null) {
                            if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false))  replaceContainerFragment(wishlistFragment.newInstance(),"wishlistFragment")
                            else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)
                        }
                        return true
                    }
                    R.id.action_chat -> {
                        if (savedInstanceState == null) {
                            if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false))  replaceContainerFragment(ChatListFragment.newInstance(),"ChatListFragment")
                            else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)
                        }
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
                if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false))  replaceContainerFragment(NotifcationFragment.newInstance(),"NotifcationFragment")
                else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)

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
                if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)) startActivity(buyerProfileIntent())
                else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)
            }
            R.id.nav_my_transactions -> {
                if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false))  startActivity(transactionIntent())
                else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)
            }
            R.id.nav_my_orders -> {
                if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false))  {
                    supportFragmentManager.beginTransaction() .add(R.id.buyer_home_container, CommonOrderFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                }
                else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)
            }
            R.id.nav_custom_design -> {
                if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)){
                    supportFragmentManager.beginTransaction() .add(R.id.buyer_home_container,OwnProductListFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                }
                else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)
            }
            R.id.nav_my_dashboard -> {
                if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false))  startActivity(dashboardIntent())
                else Utility.buyerLoginDialog(this@BuyerLandingActivity,false,0)

            }
            R.id.nav_support -> {
                val intent = Intent(this@BuyerLandingActivity, PdfViewerActivity::class.java)
                intent.putExtra("ViewType", "FAQ_PDF")
                startActivity(intent)
            }
            R.id.nav_user_manual -> {
                val intent = Intent(this@BuyerLandingActivity, PdfViewerActivity::class.java)
                intent.putExtra("ViewType", "USER_MAN_BUY")
                startActivity(intent)
            }
            R.id.nav_logout -> {
                if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)){
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
                }else{
                    startActivity(roleselectIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
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
            Log.e(TAG, "token ${token}")
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

            if(Prefs.getBoolean(ConstantsDirectory.IS_LOGGED_IN, false)){
                mProVM.getBuyerProfileDetails(this)
                mProVM.getUserMutableData()
                mViewModel.getwishlisteProductIds()
                mViewModel.getProductsInWishlist()
                mViewModel?.getMoqDeliveryTimes()
                mViewModel.getAllNotifications()
                mEnqVm?.getAllOngoingEnquiries()
                mEnqVm?.getAllCompletedEnquiries()
                mOrderVm?.getAllOngoingOrders()
                mOrderVm?.getAllCompletedOrders()
                mViewModel?.getTransactionStatus()
                mViewModel?.getChangeRequestStatuses()
                mViewModel?.getQCQuestionData()
                mViewModel?.getQCStageData()
                mViewModel?.getEscalationData()
                mViewModel?.getArtisanFaultReviewData()
                mViewModel?.getBuyerFaultReviewData()
            }
            mViewModel.getProductsOfArtisan(this)
            mViewModel.getProductUploadData()
            mViewModel.getEnquiryStageData()
            mViewModel?.getInnerEnquiryStageData()
            mViewModel.getEnquiryStageAvailableProdsData()
            mViewModel.getArtisanBrandDetails()
            mCMSViewModel?.getRegionData()
            mCMSViewModel?.categoriescodesign()
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







