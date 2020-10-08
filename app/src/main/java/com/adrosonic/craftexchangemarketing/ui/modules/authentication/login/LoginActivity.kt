package com.adrosonic.craftexchangemarketing.ui.modules.authentication.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityLoginBinding
import com.adrosonic.craftexchangemarketing.ui.modules.admin.auth.login.AdminLoginFragment
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.auth.login.ArtisanLoginPasswordFragment
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.auth.login.ArtisanLoginUsernameFragment
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.auth.login.BuyerLoginUsernameFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.loginIntent(): Intent {
    return Intent(this, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class LoginActivity : AppCompatActivity() {

    private var mBinding : ActivityLoginBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)

        when(profile){
            ConstantsDirectory.ADMIN -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.login_container,AdminLoginFragment.newInstance(profile))
                        .commitNow()
                }
            }
//            ConstantsDirectory.BUYER -> {
//                if (savedInstanceState == null) {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.login_container, BuyerLoginUsernameFragment.newInstance(profile))
//                        .commitNow()
//                }
//            }
        }
    }
}
