package com.adrosonic.craftexchangemarketing.ui.modules.authentication.reset

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ActivityResetPasswordFirstTimeBinding

fun Context.resetFirstTimeIntent(username: String, resetToken: String): Intent{
    val intent = Intent(this, ResetPasswordFirstTimeActivity::class.java)
    intent.putExtra("username", username)
    intent.putExtra("resetToken", resetToken)
    return intent
}

class ResetPasswordFirstTimeActivity : AppCompatActivity() {

    var mBinding : ActivityResetPasswordFirstTimeBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityResetPasswordFirstTimeBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.reset_container,
                            ResetPasswordFirstTimeFragment.newInstance(intent.getStringExtra("username")!!, intent.getStringExtra("resetToken")!!), "Reset Password")
                        .commit()
                }

    }
}
