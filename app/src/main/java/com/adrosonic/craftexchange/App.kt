package com.adrosonic.craftexchange

import android.app.Application
import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        val config = RealmConfiguration.Builder()
            .name("CraftExchange.realm")
            .build()

        //use this config for realm
        val mRealm = Realm.getInstance(config)

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setUseDefaultSharedPreference(true)
            .build()

    }
}