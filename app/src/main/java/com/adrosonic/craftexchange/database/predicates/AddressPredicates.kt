package com.adrosonic.craftexchange.database.predicates

import com.adrosonic.craftexchange.database.entities.Addresss
import com.adrosonic.craftexchange.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.login.User
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.EditArtisanDetails
import com.adrosonic.craftexchange.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.profile.ProfileResponse
import com.adrosonic.craftexchange.repository.data.response.buyer.login.BuyerResponse
import io.realm.Realm
import java.lang.Exception

class AddressPredicates {
    companion object {
        private var nextID : Long? = 0

        fun insertBuyerAddress(user: BuyerResponse?) : Long? {
            nextID = 0L
            val realm = Realm.getDefaultInstance()
            var addressList = user?.data?.user?.addressses
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
                                exaddr.userid = user?.data?.user?.id
                                exaddr.addrtypeid = addr.addressType.id.toString()
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
                                addrObj.userid = user?.data?.user?.id
                                addrObj.addrtypeid = addr.addressType.id.toString()
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

        fun insertArtisanAddress(user: ArtisanResponse?) : Long? {
            nextID = 0L
            val realm = Realm.getDefaultInstance()
            var addressList = user?.data?.user?.addressses
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
                                exaddr.id = addr.id
                                exaddr.userid = user?.data?.user?.id
                                exaddr.addrtypeid = addr.addressType.id.toString()
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
                                addrObj.id = addr.id
                                addrObj.userid = user?.data?.user?.id
                                addrObj.addrtypeid = addr.addressType.id.toString()
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
                realm.executeTransaction {
                    address = realm.where(Addresss::class.java)
                        .equalTo("userid", userid)
                        .and()
                        .equalTo("addrtype", addrType)
                        .findFirst()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
//                realm.close()
            }
            return address
        }

        fun editBuyerDelievryAddress(userData : EditProfileResponse, addrType: String){
            val realm = Realm.getDefaultInstance()
            var addrObj : Addresss ?= null
            var user = userData.data
            var addressList = user?.addressses
            realm.executeTransaction {
                try {


                    var addrIterator = addressList?.iterator()
                    while (addrIterator.hasNext()) {
                        var addr = addrIterator.next()

                        if(addr.addressType.addrType == addrType) {
                            addrObj = realm.where(Addresss::class.java)
                                .equalTo("userid", user.id)
                                .and()
                                .equalTo("addrtype", addrType)
                                .findFirst()

                            addrObj?.line1 = addr.line1

                            //TODO Implemented later properly with each field editable
//                            addrObj?.id = addr.id
//                            addrObj?.userid = user.id
//                            addrObj?.addrtypeid = addr.addressType.id.toString()
//                            addrObj?.addrtype = addr.addressType.addrType
//                            addrObj?.country_id = addr.country.id.toString()
//                            addrObj?.country = addr.country.name
//                            addrObj?.pincode = addr.pincode
//                            addrObj?.city = addr.city
//                            addrObj?.street = addr.street
//                            addrObj?.district = addr.district
//                            addrObj?.state = addr.state
//                            addrObj?.landmark = addr.landmark
//                            addrObj?.line2 = addr.line2

                            realm.copyToRealmOrUpdate(addrObj)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
//                realm.close()
                }
            }
        }

        fun editArtisanAddress(userid : String, userData : EditArtisanDetails){
            var realm = Realm.getDefaultInstance()
            var address: Addresss?
            try {
                realm.executeTransaction {
                    var address = realm.where(Addresss::class.java)
                        .equalTo("userid", userid.toLong())
                        .findFirst()

                    //TODO implement proper address edit
//                    address?.country_id = userData.country.id.toString()
//                    address?.line1 = userData.line1
//                    address?.state = userData.state
//                    address?.pincode = userData.pincode
//                    address?.district = userData.district

                    address?.line1 = userData.line1


                    realm.copyToRealmOrUpdate(address)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
//                realm.close()
            }
//            return address
        }

        fun refreshUserAddress(userData: ProfileResponse?){
            val realm = Realm.getDefaultInstance()
            var addressList = userData?.data?.user?.addressses
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

                                addrObj?.id = addr.id
                                addrObj?.userid = userData?.data?.user?.id
                                addrObj?.addrtypeid = addr.addressType.id.toString()
                                addrObj?.addrtype = addr.addressType.addrType
                                addrObj?.country_id = addr.country.id.toString()
                                addrObj?.country = addr.country.name
                                addrObj?.pincode = addr.pincode
                                addrObj?.city = addr.city
                                addrObj?.street = addr.street
                                addrObj?.district = addr.district
                                addrObj?.state = addr.state
                                addrObj?.landmark = addr.landmark
                                addrObj?.line1 = addr.line1
                                addrObj?.line2 = addr.line2

                                realm.copyToRealmOrUpdate(addrObj)

                        }
                    }
                }
            } catch (e: Exception) {
            } finally {
//                realm.close()
            }
        }
    }
}