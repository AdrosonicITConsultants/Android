package com.adrosonic.craftexchange.database

import io.realm.Realm
import io.realm.RealmConfiguration

class CXRealmManager {
    companion object{
        val REALM_DB = "CraftExchange.realm"
        val DB_VERSION = 1L
        var realmConfiguration: RealmConfiguration?= null

        init {
            if (realmConfiguration == null) {
                realmConfiguration = RealmConfiguration.Builder()
                    .name(REALM_DB)
                    .modules(CXRealmModule())
                    .schemaVersion(DB_VERSION)
                    .deleteRealmIfMigrationNeeded()
                    .build()
                Realm.setDefaultConfiguration(realmConfiguration)
            }

        }

        fun getRealmInstance(): Realm {
            return Realm.getInstance(realmConfiguration)
        }
    }
}