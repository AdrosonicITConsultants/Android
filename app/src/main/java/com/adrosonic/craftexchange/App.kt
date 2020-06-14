package com.adrosonic.craftexchange

import android.app.Application
import android.content.ContextWrapper
import com.pixplicity.easyprefs.library.Prefs
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {

    val REALM_DB_NAME = "CraftExchange.realm"
    val DB_VERSION = 1L

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name(REALM_DB_NAME)
            .schemaVersion(DB_VERSION)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
        //use this config for realm
//        val mRealm = Realm.getInstance(config)

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setUseDefaultSharedPreference(true)
            .build()

    }
}