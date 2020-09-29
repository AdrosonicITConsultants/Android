package com.adrosonic.craftexchange.database.predicates

import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftAdmin
import com.adrosonic.craftexchange.repository.data.response.admin.login.AdminResponse

class AdminPredicates {

    companion object {
        var nextID: Long? = 0

        fun insertAdmin(userData : AdminResponse?) : Long? {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var user = userData?.data?.user


            realm.executeTransaction {

                var userObj =realm.where(CraftAdmin::class.java)
                    .equalTo("id", userData?.data?.user?.id)
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
                    exUser.acctoken = userData?.data?.acctoken
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
                    userObj.acctoken = userData?.data?.acctoken
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

    }
}