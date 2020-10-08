package com.adrosonic.craftexchangemarketing.ui.modules.authentication.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityRegisterBinding
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.auth.register.ArtisanRegisterArtisanidFragment
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.auth.register.BuyerRegisterUsernameFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.registerIntent(): Intent {
    return Intent(this, RegisterActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class RegisterActivity : AppCompatActivity() {

    private var mBinding : ActivityRegisterBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)

        when(profile){
            ConstantsDirectory.ARTISAN -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.register_container, ArtisanRegisterArtisanidFragment.newInstance())
                        .commitNow()
                }
            }
            ConstantsDirectory.BUYER -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.register_container, BuyerRegisterUsernameFragment.newInstance())
//                        .replace(R.id.register_container, BuyerRegisterCompanyFragment.newInstance())
                        .commitNow()
                }
            }
        }
    }
}

