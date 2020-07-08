package com.adrosonic.craftexchange.ui.modules.buyer.landing

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityBuyerLandingBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.buyerProfileIntent
import com.adrosonic.craftexchange.ui.modules.landing_com.LandingViewModel
import com.adrosonic.craftexchange.ui.modules.role.roleselectIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_buyer_landing.*
import kotlinx.android.synthetic.main.nav_header_landing.view.*

fun Context.buyerLandingIntent(): Intent {
    return Intent(this, BuyerLandingActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class BuyerLandingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

//    fun AppCompatActivity.replaceContainerFragment(fragment: androidx.fragment.app.Fragment) {
//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
//    }

    private var mBinding : ActivityBuyerLandingBinding ?= null
    private var mViewModel: LandingViewModel? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityBuyerLandingBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        var imageName = Utility.craftUser?.brandLogo
        var url = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${Prefs.getString(ConstantsDirectory.USER_ID,"")}/CompanyDetails/Logo/${imageName}"
        ImageSetter.setImage(applicationContext,url,nav_view.getHeaderView(0).logo,
            R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)

        mViewModel = ViewModelProviders.of(this).get(LandingViewModel::class.java)

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
        var lastname = Prefs.getString(ConstantsDirectory.LAST_NAME,"User")

        var username = "$firstname $lastname"

        mBinding?.navView?.setNavigationItemSelectedListener(this)
//        mBinding?.navView?.menu?.getItem(0)?.isChecked = false
        nav_view.getHeaderView(0).text_user.text = username

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.buyer_home_container,
                    BuyerHomeFragment.newInstance()
                )
                .commitNow()
        }

        tab_bar.onNavigationItemSelectedListener = object: BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(@NonNull item:MenuItem):Boolean {
                when (item.itemId) {
                    R.id.action_home -> {
                        if (savedInstanceState == null) {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.buyer_home_container,
                                    BuyerHomeFragment.newInstance()
                                )
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.tool_menu, menu)

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivity(buyerProfileIntent())
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
            this.finish()
        }
    }

}
