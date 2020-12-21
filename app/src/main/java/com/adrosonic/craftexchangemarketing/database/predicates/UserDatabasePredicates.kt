package com.adrosonic.craftexchangemarketing.database.predicates

import android.util.Log
import com.adrosonic.craftexchangemarketing.database.CXRealmManager
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.AdminProductCatalogue
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.UserAddress
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.UserDatabase
import com.adrosonic.craftexchangemarketing.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.request.editProfileModel.EditArtisanDetails
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.buyer.login.BuyerResponse
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.lang.Exception

class UserDatabasePredicates {
    companion object {
        private var nextID : Long? = 0

        fun insertUserDatabase(users: List<User>?,isArtisan:Boolean) : Long? {
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()

            try {
                realm.executeTransaction {
                    var usersIterator = users?.iterator()
                    if (usersIterator != null) {
                        while (usersIterator.hasNext()) {
                            var user = usersIterator.next()
                            var userObj = realm.where(UserDatabase::class.java)
                                .equalTo("id", user.id)
                                .limit(1)
                                .findFirst()

                            if (userObj == null) {
                                var primId = it.where(UserDatabase::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var newUser = it.createObject(UserDatabase::class.java, nextID)
                                newUser.id = user.id
                                newUser.weaverId = user.weaverId
                                newUser.rating = user.rating
                                newUser.status = user.status
                                newUser.email = user.email
                                newUser.cluster = user.cluster
                                newUser.brandName = user.brandName
                                newUser.firstName = user.firstName
                                newUser.lastName = user.lastName
                                newUser.dateAdded = user.dateAdded
                                newUser.mobile = user.mobile
                                newUser.isArtisan = isArtisan

                                realm.copyToRealmOrUpdate(newUser)
                            } else {
                                nextID = userObj._id ?: 0
                                userObj.id = user.id
                                userObj.weaverId = user.weaverId
                                userObj.rating = user.rating
                                userObj.status = user.status
                                userObj.email = user.email
                                userObj.cluster = user.cluster
                                userObj.brandName = user.brandName
                                userObj.firstName = user.firstName
                                userObj.lastName = user.lastName
                                userObj.dateAdded = user.dateAdded
                                userObj.mobile = user.mobile
                                userObj.isArtisan = isArtisan
                                realm.copyToRealmOrUpdate(userObj)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            } finally {
//                realm.close()
            }
            return nextID
        }

        fun getUsers(isArtisan: Boolean?,search:String,clusterstr: String,rating:Float,filterBy1:String,sort:Sort?): List<UserDatabase>? {

            var realm = CXRealmManager.getRealmInstance()
            var users: List<UserDatabase>? =null
            val cluster= if(clusterstr.equals("Select Cluster"))"" else clusterstr
            val filterBy=if(filterBy1.isNullOrEmpty())"dateAdded" else filterBy1
            realm.executeTransaction {
                Log.e("Filter","search $search: rating $rating")
                users = if(search.isNullOrEmpty()&& cluster.isEmpty()){
                        Log.e("Filter","11111111111 $filterBy: sort $sort")
                        realm.where(UserDatabase::class.java)
                            .equalTo(UserDatabase.COLUMN_IS_ARTISAN, true)
                            .and().greaterThan(UserDatabase.COLUMN_RATING,rating)
                            .and().sort(filterBy, sort)
                            .findAll().toList()

                    }
                    else {
                    if(cluster.isEmpty()){
                            Log.e("Filter","222222222 $filterBy: rating $rating")
                            realm.where(UserDatabase::class.java)
                                .equalTo(UserDatabase.COLUMN_IS_ARTISAN, true)
                                .and().greaterThan(UserDatabase.COLUMN_RATING,rating)
                                .findAll().where()
                                .contains(UserDatabase.COLUMN_BRAND_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_FIRST_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_LAST_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_EMAIL,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_MOBILE,search, Case.INSENSITIVE)
//                              .contains(UserDatabase.COLUMN_ID,search, Case.INSENSITIVE).or()
                                .and().sort(filterBy, sort)
                                .findAll().toList()
                    }
                    else {
                            Log.e("Filter","333333333 $filterBy: rating $rating")
                            realm.where(UserDatabase::class.java).equalTo( UserDatabase.COLUMN_IS_ARTISAN, true)
                                .and().greaterThan(UserDatabase.COLUMN_RATING,rating)
                                .and().equalTo(UserDatabase.COLUMN_CLUSTER,cluster, Case.INSENSITIVE)
                                .findAll().where()
                                .contains(UserDatabase.COLUMN_BRAND_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_FIRST_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_LAST_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_EMAIL,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_MOBILE,search, Case.INSENSITIVE)
//                              .contains(UserDatabase.COLUMN_ID,search, Case.INSENSITIVE).or()
                                .and().sort(filterBy, sort)
                                .findAll().toList()
                    }
                }
            }
            Log.e("Filter","4444444 count: ${users?.size}")
            return users
        }

        fun getBuyerUsers(search:String,rating:Float,filterBy:String,sort:Sort?): List<UserDatabase>? {
            var realm = CXRealmManager.getRealmInstance()
            var users: List<UserDatabase>? =null
            realm.executeTransaction {
                Log.e("Filter","search $search: rating $rating")
                users = if(search.isNullOrEmpty()){
                    if(filterBy.isNotEmpty()){
                        Log.e("Filter","11111111111 $filterBy: sort $sort")
                        realm.where(UserDatabase::class.java)
                            .equalTo(UserDatabase.COLUMN_IS_ARTISAN, false)
                            .and().greaterThan(UserDatabase.COLUMN_RATING,rating)
                            .and().sort(filterBy, sort)
                            .findAll().toList()
                    }
                    else {
                        Log.e("Filter","222222222222 $filterBy: rating $rating")
                        realm.where(UserDatabase::class.java)
                            .equalTo(UserDatabase.COLUMN_IS_ARTISAN, false)
                            .and().greaterThan(UserDatabase.COLUMN_RATING,rating)
                            .and().sort(UserDatabase.COLUMN_DATE_ADDED, Sort.DESCENDING)
                            .findAll().toList()
                    }
                }
                else {
                        if(filterBy.isNotEmpty()){
                            Log.e("Filter","333333333333 $filterBy: rating $rating")
                            realm.where(UserDatabase::class.java)
                                .equalTo(UserDatabase.COLUMN_IS_ARTISAN, false)
                                .and().greaterThan(UserDatabase.COLUMN_RATING,rating)
                                .and().sort(filterBy, sort)
                                .findAll().where()
                                .contains(UserDatabase.COLUMN_BRAND_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_FIRST_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_LAST_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_EMAIL,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_MOBILE,search, Case.INSENSITIVE)
//                            .contains(UserDatabase.COLUMN_ID,search, Case.INSENSITIVE).or()
                                .findAll().toList()
                        }
                        else{
                            Log.e("Filter","4444444444 $filterBy: rating $rating")
                            realm.where(UserDatabase::class.java)
                                .equalTo(UserDatabase.COLUMN_IS_ARTISAN, false)
                                .and().greaterThan(UserDatabase.COLUMN_RATING,rating)
                                .findAll().where()
                                .contains(UserDatabase.COLUMN_BRAND_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_FIRST_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_LAST_NAME,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_EMAIL,search, Case.INSENSITIVE).or()
                                .contains(UserDatabase.COLUMN_MOBILE,search, Case.INSENSITIVE)
//                            .contains(UserDatabase.COLUMN_ID,search, Case.INSENSITIVE).or()
                                .findAll().toList()
                        }
                    }
            }
            Log.e("Filter","5555555 count: ${users?.size}")
            return users
        }


    }
}