package com.adrosonic.craftexchange.ui.modules.landing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pixplicity.easyprefs.library.Prefs

class LandingViewModel(application: Application) : AndroidViewModel(application) {

    fun logoutUser() {
        val editor = Prefs.edit()
        editor.clear()
        editor.commit()
        editor.apply()
    }
}