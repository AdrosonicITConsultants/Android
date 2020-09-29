package com.adrosonic.craftexchange.ui.modules.authentication.reset

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityResetPasswordBinding

fun Context.resetIntent(): Intent {
    return Intent(this,ResetPasswordActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class ResetPasswordActivity : AppCompatActivity() {

    var mBinding : ActivityResetPasswordBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.reset_container,
                            ResetUsernameFragment.newInstance(),"Reset Username")
                        .commit()
                }



    }
}
