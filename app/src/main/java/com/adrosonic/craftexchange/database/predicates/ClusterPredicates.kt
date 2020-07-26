package com.adrosonic.craftexchange.database.predicates

import android.util.Log
import com.adrosonic.craftexchange.database.CXRealmManager
import com.adrosonic.craftexchange.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.CLusterResponse
import io.realm.Realm
import io.realm.RealmResults
import java.lang.Exception

class ClusterPredicates {
    companion object{
        private var nextID: Long? = 0

        fun insertClusters(clusters : CLusterResponse?){
            nextID = 0L
            val realm = CXRealmManager.getRealmInstance()
            var clusterList = clusters?.data
            realm.executeTransaction {
                try {
                    var clusterIterator = clusterList?.iterator()
                    if(clusterIterator != null){
                        while (clusterIterator.hasNext()){
                            var cluster =clusterIterator.next()
                            var clusterObj = realm.where(ClusterList::class.java)
                                .equalTo("clusterid", cluster.id)
                                .limit(1)
                                .findFirst()

                            if(clusterObj == null) {
                                var primId = it.where(ClusterList::class.java).max("_id")
                                if (primId == null) {
                                    nextID = 1
                                } else {
                                    nextID = primId.toLong() + 1
                                }
                                var excluster = it.createObject(ClusterList::class.java, nextID)
                                excluster?.clusterid = cluster.id
                                excluster?.clusterDesc = cluster.desc
                                excluster?.adjective = cluster.adjective

                                realm.copyToRealmOrUpdate(excluster)
                            }else {
                                nextID = clusterObj._id ?: 0
                                clusterObj?.clusterid = cluster.id
                                clusterObj?.clusterDesc = cluster.desc
                                clusterObj?.adjective = cluster.adjective

                                realm.copyToRealmOrUpdate(clusterObj)
                            }
                        }
                    }
                }catch (e : Exception){
                    //TODO Print logs
                    Log.e("Error Cluster",e.toString())
                }
            }
        }

        fun getAllClusters(): RealmResults<ClusterList>? {
            val realm = CXRealmManager.getRealmInstance()
            return realm.where(ClusterList::class.java).findAll()
        }
    }
}