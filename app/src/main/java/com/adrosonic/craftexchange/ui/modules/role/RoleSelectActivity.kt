package com.adrosonic.craftexchange.ui.modules.role

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityRoleSelectBinding

fun Context.roleselectIntent(): Intent {
    return Intent(this, RoleSelectActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

class RoleSelectActivity : LocaleBaseActivity(){

    private var mBinding : ActivityRoleSelectBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRoleSelectBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.role_select_container,
                    RoleSelectFragment.newInstance(),"RoleSelect")
                .commit()
        }
    }
}
