package com.adrosonic.craftexchange.syncManager

import android.content.Context
import android.util.Log
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.syncManager.processor.ProductDeleter
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType
import com.adrosonic.craftexchange.syncManager.processor.ProductAdd
import com.adrosonic.craftexchange.syncManager.processor.ProductUpdate
import com.adrosonic.craftexchange.syncManager.processor.wishlist.WishlistAdd

/**
 * Created by Rital Naik on 29/07/2020.
 */
class SyncCoordinator(val context: Context) {
    val processors = arrayOf(
        ProductAdd(),
        ProductUpdate(),
        ProductDeleter(),
        WishlistAdd()
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
