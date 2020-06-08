package com.adrosonic.craftexchange.ui.modules.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityMainBinding
import com.adrosonic.craftexchange.ui.modules.authentication.login.loginIntent
import com.adrosonic.craftexchange.ui.modules.role.roleselectIntent

fun Context.mainIntent(): Intent {
    return Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class MainActivity : AppCompatActivity() {
    private var mBinding : ActivityMainBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        startActivity(roleselectIntent())
    }
}
