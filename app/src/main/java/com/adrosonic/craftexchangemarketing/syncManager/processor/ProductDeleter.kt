package com.adrosonic.craftexchangemarketing.syncManager.processor

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.services.ProductDeletingService
import com.adrosonic.craftexchangemarketing.syncManager.processor.objects.ItemType

/**
 * Created by 'Rital Naik on 15/05/18.
 */
class ProductDeleter: ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { deleteProduct(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${ArtisanProducts.COLUMN_ACTION_DELETE}=1"


    fun deleteProduct(itemType: ItemType, context: Context) {
        val work = Intent().apply { putExtra(ProductDeletingService.KEY_ID, itemType.id) }
        ProductDeletingService.enqueueWork(context, work)
    }

    fun markItemAsComplete(id: String) {
        elementsInProgress.markObjectsAsComplete(arrayListOf(ItemType(id)))
    }
}