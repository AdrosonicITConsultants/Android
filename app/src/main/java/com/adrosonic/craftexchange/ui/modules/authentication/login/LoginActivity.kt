package com.adrosonic.craftexchange.ui.modules.authentication.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityLoginBinding
import com.adrosonic.craftexchange.ui.modules.artisan.authentication.login.ArtisanLoginUsernameFragment
import com.adrosonic.craftexchange.ui.modules.buyer.authentication.login.BuyerLoginUsernameFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_login.*

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
            ConstantsDirectory.ARTISAN -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.login_container, ArtisanLoginUsernameFragment.newInstance(profile))
                        .commitNow()
                }
            }
            ConstantsDirectory.BUYER -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.login_container, BuyerLoginUsernameFragment.newInstance(profile))
                        .commitNow()
                }
            }
        }
    }
}
