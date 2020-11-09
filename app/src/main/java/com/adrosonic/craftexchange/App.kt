package com.adrosonic.craftexchange

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.adrosonic.craftexchange.LocalizationManager.LocaleManager
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.utils.registerNetworkChangeReceiver
import com.pixplicity.easyprefs.library.Prefs
import io.realm.Realm


class App : Application() {

    var coordinator: SyncCoordinator? = null
    var networkChangeReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Prefs.Builder()
            .setContext(this)
//            .setMode(ContextWrapper.MODE_PRIVATE)
            .setUseDefaultSharedPreference(true)
            .build()
        coordinator = SyncCoordinator(applicationContext)
        coordinator?.performLocallyAvailableActions()
        networkChangeReceiver = applicationContext.registerNetworkChangeReceiver(
            onNetworkConnected = {
                coordinator?.performLocallyAvailableActions()
                Log.i("Network", "Connected")
            },
            onNetworkDisconnected = {
                Log.i("Network", "DisConnected")
            })
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(base!!))
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
    fun callOfflineSync() {
        if (Utility.checkIfInternetConnected(this)){
            try {
                coordinator?.performLocallyAvailableActions()
            } catch (e: Exception) {
                Log.e("App Exception", e.message)
            }
        }
    }
}