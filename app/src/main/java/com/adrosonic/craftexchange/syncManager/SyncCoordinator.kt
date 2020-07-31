package com.adrosonic.craftexchange.syncManager

import android.content.Context
import android.util.Log
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.syncManager.processor.ProductDeleter
import com.adrosonic.craftexchange.syncManager.processor.objects.ItemType
import com.adrosonic.craftexchange.syncManager.processor.ProductAdd
import com.adrosonic.craftexchange.syncManager.processor.ProductUpdate

/**
 * Created by Rital Naik on 29/07/2020.
 */
class SyncCoordinator(val context: Context) {
    val processors = arrayOf(
        ProductAdd(),
        ProductUpdate(),
        ProductDeleter()
    )

    fun performLocallyAvailableActions() {
        processors.forEach {
            val list: MutableList<ItemType> = arrayListOf()
            try {
                val queue =  ProductPredicates.getProductMarkedForActions(it.predicateForLocallyTrackedElements)
                val iterator = queue?.iterator()
                while (iterator!!.hasNext()) {
                    list.add(ItemType(iterator.next().toString()))
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
