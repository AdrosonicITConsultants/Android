package com.adrosonic.craftexchange.utils

import com.adrosonic.craftexchange.database.entities.CraftUser
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.pixplicity.easyprefs.library.Prefs

class UserConfig {
    companion object{

        fun getUser(): CraftUser? {
            var user = UserPredicates.findUser(Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
            return user
        }

    }
}