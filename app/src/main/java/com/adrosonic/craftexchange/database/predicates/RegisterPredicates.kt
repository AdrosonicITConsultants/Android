package com.adrosonic.craftexchange.database.predicates

import io.realm.Realm
import java.lang.Exception

class RegisterPredicates{
    companion object {
        var nextID : Long? = 0

        fun insertAllCountries(){
            var realm = Realm.getDefaultInstance()
            try {
                realm?.executeTransaction {

                }
            }catch(e : Exception){
            }finally{
                realm.close()
            }
        }

    }
}