package com.adrosonic.craftexchange.ui.modules.authentication.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityLoginBinding
import com.adrosonic.craftexchange.ui.modules.buyer.authentication.login.BuyerLoginUsernameFragment

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

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.login_container,
                    BuyerLoginUsernameFragment.newInstance(),"Login Buyer Username")
                .addToBackStack(null)
                .commit()
        }
    }
}
