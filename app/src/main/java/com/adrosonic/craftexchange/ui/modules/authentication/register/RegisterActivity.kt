package com.adrosonic.craftexchange.ui.modules.authentication.register

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityRegisterBinding
import com.adrosonic.craftexchange.ui.modules.buyer.authentication.register.BuyerRegisterUsernameFragment

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

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.register_container,
                    BuyerRegisterUsernameFragment.newInstance(),"Register Buyer Username")
                .addToBackStack(null)
                .commit()
        }
    }
}

