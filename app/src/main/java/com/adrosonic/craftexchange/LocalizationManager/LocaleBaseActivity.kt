package com.adrosonic.craftexchange.LocalizationManager

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.adrosonic.craftexchange.LocalizationManager.LocaleManager.getLanguagePref
import com.adrosonic.craftexchange.LocalizationManager.LocaleManager.setLocale
import java.util.*


abstract class LocaleBaseActivity : AppCompatActivity() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        resetTitles()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(setLocale(base))
    }
    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        val locale = Locale(getLanguagePref(this))
        Locale.setDefault(locale)
        overrideConfiguration.setLocale(locale)
        super.applyOverrideConfiguration(overrideConfiguration)
    }
    protected fun resetTitles() {
        try {
            val info = packageManager.getActivityInfo(
                componentName, PackageManager.GET_META_DATA )
            if (info.labelRes != 0) {
                setTitle(info.labelRes)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}