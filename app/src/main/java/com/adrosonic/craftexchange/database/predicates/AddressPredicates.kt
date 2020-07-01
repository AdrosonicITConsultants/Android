package com.adrosonic.craftexchange.database.predicates

import com.adrosonic.craftexchange.database.entities.Addresss
import com.adrosonic.craftexchange.database.entities.CraftUser
import com.adrosonic.craftexchange.repository.data.loginResponse.buyer.User
import io.realm.Realm
import java.lang.Exception
import kotlin.math.log

class AddressPredicates {
    companion object {
        private var nextID : Long? = 0

        fun insertBuyerAddress(user: User?) : Long? {
            nextID = 0L
            val realm = Realm.getDefaultInstance()
            var addressList = user?.addressses
            try {
                realm?.executeTransaction {
                    var addrIterator = addressList?.iterator()
                    if (addrIterator != null) {
                        while (addrIterator.hasNext()) {
                            var addr = addrIterator.next()
                            var addrObj = realm.where(Addresss::class.java)
                                .equalTo("id", addr.id)
                                .limit(1)
                                .findFirst()

                            if (addrObj == null) {
                                var primId = it.where(Addresss::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exaddr = it.createObject(Addresss::class.java, nextID)
                                exaddr.id = addr.id.toLong()
                                exaddr.userid = user?.id
                                exaddr.addrid = addr.addressType.id.toString()
                                exaddr.addrtype = addr.addressType.addrType
                                exaddr.country_id = addr.country.id.toString()
                                exaddr.country = addr.country.name
                                exaddr.pincode = addr.pincode
                                exaddr.city = addr.city
                                exaddr.street = addr.street
                                exaddr.district = addr.district
                                exaddr.state = addr.state
                                exaddr.landmark = addr.landmark
                                exaddr.line1 = addr.line1
                                exaddr.line2 = addr.line2

                                realm.copyToRealmOrUpdate(exaddr)
                            } else {
                                nextID = addrObj._id ?: 0
                                addrObj.id = addr.id.toLong()
                                addrObj.userid = user?.id
                                addrObj.addrid = addr.addressType.id.toString()
                                addrObj.addrtype = addr.addressType.addrType
                                addrObj.country_id = addr.country.id.toString()
                                addrObj.country = addr.country.name
                                addrObj.pincode = addr.pincode
                                addrObj.city = addr.city
                                addrObj.street = addr.street
                                addrObj.district = addr.district
                                addrObj.state = addr.state
                                addrObj.landmark = addr.landmark
                                addrObj.line1 = addr.line1
                                addrObj.line2 = addr.line2

                                realm.copyToRealmOrUpdate(addrObj)
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

        fun insertArtisanAddress(user: com.adrosonic.craftexchange.repository.data.loginResponse.artisan.User?) : Long? {
            nextID = 0L
            val realm = Realm.getDefaultInstance()
            var addressList = user?.addressses
            try {
                realm?.executeTransaction {
                    var addrIterator = addressList?.iterator()
                    if (addrIterator != null) {
                        while (addrIterator.hasNext()) {
                            var addr = addrIterator.next()
                            var addrObj = realm.where(Addresss::class.java)
                                .equalTo("id", addr.id)
                                .limit(1)
                                .findFirst()

                            if (addrObj == null) {
                                var primId = it.where(Addresss::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var exaddr = it.createObject(Addresss::class.java, nextID)
                                exaddr.id = addr.id.toLong()
                                exaddr.userid = user?.id
                                exaddr.addrid = addr.addressType.id.toString()
                                exaddr.addrtype = addr.addressType.addrType
                                exaddr.country_id = addr.country.id.toString()
                                exaddr.country = addr.country.name
                                exaddr.pincode = addr.pincode
                                exaddr.city = addr.city
                                exaddr.street = addr.street
                                exaddr.district = addr.district
                                exaddr.state = addr.state
                                exaddr.landmark = addr.landmark
                                exaddr.line1 = addr.line1
                                exaddr.line2 = addr.line2

                                realm.copyToRealmOrUpdate(exaddr)
                            } else {
                                nextID = addrObj._id ?: 0
                                addrObj.id = addr.id.toLong()
                                addrObj.userid = user?.id
                                addrObj.addrid = addr.addressType.id.toString()
                                addrObj.addrtype = addr.addressType.addrType
                                addrObj.country_id = addr.country.id.toString()
                                addrObj.country = addr.country.name
                                addrObj.pincode = addr.pincode
                                addrObj.city = addr.city
                                addrObj.street = addr.street
                                addrObj.district = addr.district
                                addrObj.state = addr.state
                                addrObj.landmark = addr.landmark
                                addrObj.line1 = addr.line1
                                addrObj.line2 = addr.line2

                                realm.copyToRealmOrUpdate(addrObj)
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

        fun getAddressAddrType(addrType : String, userid : Long) : Addresss? {
            var realm = Realm.getDefaultInstance()
            var address : Addresss ?= null
            try {
                address = realm.where(Addresss::class.java)
                    .equalTo("userid", userid)
                    .and()
                    .equalTo("addrtype",addrType)
                    .findFirst()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
//                realm.close()
            }
            return address
        }
    }
}