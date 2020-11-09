package com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.adrosonic.craftexchange.LocalizationManager.LocaleBaseActivity
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityArtisanEditProfileBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.buyerProfileIntent
import com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile.BrandEditFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.artisanEditProfileIntent(): Intent {
    return Intent(this, ArtisanEditProfileActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        Intent.FLAG_ACTIVITY_NEW_TASK or
    }
}

class ArtisanEditProfileActivity : LocaleBaseActivity() {

    companion object;

    private var mBinding : ActivityArtisanEditProfileBinding ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanEditProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        var welcome_text = getString(R.string.hello)+" ${Prefs.getString(ConstantsDirectory.FIRST_NAME,"User")}"
        mBinding?.artisanName?.text = welcome_text

        var section = intent.getStringExtra("Section")

        when(section){
            "Details" -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.artisan_edit_profile_container, MyDetailsEditProfileFragment.newInstance())
                        .commitNow()
                }
            }
            "Brand" -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.artisan_edit_profile_container, BrandDetailsEditFragment.newInstance())
                        .commitNow()
                }
            }
            "Bank" -> {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.artisan_edit_profile_container, BankEditFragment.newInstance())
                        .commitNow()
                }
            }
        }

        mBinding?.btnBack?.setOnClickListener {
            onBackPressed()
            this.finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}
