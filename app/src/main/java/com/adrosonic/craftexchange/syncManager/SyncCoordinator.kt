package com.adrosonic.craftexchange.syncManager

import android.content.Context
import android.util.Log
import com.adrosonic.craftexchange.database.predicates.*
import com.adrosonic.craftexchange.syncManager.processor.ProductDeleter
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType
import com.adrosonic.craftexchange.syncManager.processor.ProductAdd
import com.adrosonic.craftexchange.syncManager.processor.ProductUpdate
import com.adrosonic.craftexchange.syncManager.processor.customDesign.OwnDesignAdd
import com.adrosonic.craftexchange.syncManager.processor.customDesign.OwnDesignDelete
import com.adrosonic.craftexchange.syncManager.processor.customDesign.OwnDesignUpdate
import com.adrosonic.craftexchange.syncManager.processor.moq.SendMoqAction
import com.adrosonic.craftexchange.syncManager.processor.notification.NotificationAction
import com.adrosonic.craftexchange.syncManager.processor.pi.PiActions
import com.adrosonic.craftexchange.syncManager.processor.wishlist.WishlistAction

/**
 * Created by Rital Naik on 29/07/2020.
 */
class SyncCoordinator(val context: Context) {
    val processors = arrayOf(
        ProductAdd(),
        ProductUpdate(),
        ProductDeleter(),
        WishlistAction(),
        OwnDesignAdd(),
        OwnDesignUpdate(),
        OwnDesignDelete(),
        NotificationAction(),
        SendMoqAction(),
        PiActions()
    )

    fun performLocallyAvailableActions() {
        processors.forEach {
            val list: MutableList<ItemType> = arrayListOf()
            try {
                val queue1 =  ProductPredicates.getProductMarkedForActions(it.predicateForLocallyTrackedElements)
                val iterator1 = queue1?.iterator()
                if (iterator1 != null) {
                    while (iterator1.hasNext()) {
                        list.add(ItemType(iterator1.next().toString()))
                    }
                }
                val queue2 =  WishlistPredicates.getProductMarkedForActions(it.predicateForLocallyTrackedElements)
                val iterator2 = queue2?.iterator()
                if (iterator2 != null) {
                    while (iterator2.hasNext()) {
                        list.add(ItemType(iterator2.next().toString()))
                    }
                }
                val queue3 =  BuyerCustomProductPredicates.getProductMarkedForActions(it.predicateForLocallyTrackedElements)
                val iterator3 = queue3?.iterator()
                if (iterator3 != null) {
                    while (iterator3.hasNext()) {
                        list.add(ItemType(iterator3.next().toString()))
                    }
                }
                val queue4=  NotificationPredicates.getNotificationMarkedForActions(it.predicateForLocallyTrackedElements)
                val iterator4 = queue4?.iterator()
                if (iterator4 != null) {
                    while (iterator4.hasNext()) {
                        list.add(ItemType(iterator4.next().toString()))
                    }
                }
                val queue5=  MoqsPredicates.getMoqMarkedForActions(it.predicateForLocallyTrackedElements)
                val iterator5 = queue5?.iterator()
                if (iterator5 != null) {
                    while (iterator5.hasNext()) {
                        list.add(ItemType(iterator5.next().toString()))
                    }
                }
                val queue6=  PiPredicates.getPiMarkedForActions(it.predicateForLocallyTrackedElements)
                Log.e("Offline","itemId :${queue6?.joinToString()}")
                val iterator6 = queue6?.iterator()
                if (iterator6 != null) {
                    while (iterator6.hasNext()) {
                        list.add(ItemType(iterator6.next().toString()))
                    }
                }
            } catch (e: Exception) {
            }
            try {
                Log.e("Offline", "SynCoordinator items: " + list.size)
                it.processChangedLocalElements(list, context)
            } catch (e: Exception) {
                Log.e("SynCoordinator", e.message)
            }
        }
    }
}
