package com.adrosonic.craftexchangemarketing.database.predicates

import android.util.Log
import com.adrosonic.craftexchangemarketing.database.CXRealmManager
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CategoryProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CraftAdmin
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.login.AdminResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.login.Data
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.login.LoginData
import com.adrosonic.craftexchangemarketing.repository.data.response.escalation.userData
import java.lang.Exception

class AdminPredicates {

    companion object {
        var nextID: Long? = 0

        fun insertAdmin(data : LoginData) : Long? {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var user = data.user


            realm.executeTransaction {

                var userObj =realm.where(CraftAdmin::class.java)
                    .equalTo("id", data.user?.id)
                    .limit(1)
                    .findFirst()

//                var uniqueId = try { userObj!!.id?:"" }catch (e: Exception){ e.printStackTrace() }

                if(userObj == null) {
                    var primId = it.where(CraftAdmin::class.java).max("_id")
                    if (primId == null) {
                        nextID = 1
                    } else {
                        nextID = primId.toLong() + 1
                    }
                    var exUser = it.createObject(CraftAdmin::class.java, nextID)
                    exUser.id = user?.id
                    exUser.acctoken = data.acctoken
                    exUser.email = user?.email
                    exUser.username = user?.username
                    exUser.refMarketingRoleId = user?.refMarketingRoleId
                    exUser.statusId = user?.statusId
                    exUser.createdOn = user?.createdOn
                    exUser.modifiedOn = user?.modifiedOn
                    exUser.enabled = user?.enabled
                    exUser.authorities = user?.authorities
                    exUser.accountNonExpired = user?.accountNonExpired
                    exUser.accountNonLocked = user?.accountNonLocked
                    exUser.credentialsNonExpired = user?.credentialsNonExpired

                    realm.copyToRealmOrUpdate(exUser)

                }else{
                    nextID = userObj._id ?:0

                    userObj.id = user?.id
                    userObj.acctoken = data.acctoken
                    userObj.email = user?.email
                    userObj.username = user?.username
                    userObj.refMarketingRoleId = user?.refMarketingRoleId
                    userObj.statusId = user?.statusId
                    userObj.createdOn = user?.createdOn
                    userObj.modifiedOn = user?.modifiedOn
                    userObj.enabled = user?.enabled
                    userObj.authorities = user?.authorities
                    userObj.accountNonExpired = user?.accountNonExpired
                    userObj.accountNonLocked = user?.accountNonLocked
                    userObj.credentialsNonExpired = user?.credentialsNonExpired

                    realm.copyToRealmOrUpdate(userObj)
                }
            }
            return nextID!!
        }

        fun deleteData(){
            val realm = CXRealmManager.getRealmInstance()
            realm?.executeTransaction {
                try {

                    realm?.where(CraftAdmin::class.java).findAll().deleteAllFromRealm()
                    // TODO: 06-10-2020 remove all the data stored

                }catch (e: Exception){
                    Log.e("DeleteData","${e.printStackTrace()}")
                }
            }
        }


    }
}