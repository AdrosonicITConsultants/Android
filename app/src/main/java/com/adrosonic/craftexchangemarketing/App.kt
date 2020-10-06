package com.adrosonic.craftexchangemarketing

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ContextWrapper
import android.util.Log
import com.adrosonic.craftexchangemarketing.syncManager.SyncCoordinator
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.utils.registerNetworkChangeReceiver
import com.pixplicity.easyprefs.library.Prefs
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {

    var coordinator: SyncCoordinator? = null
    var networkChangeReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
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