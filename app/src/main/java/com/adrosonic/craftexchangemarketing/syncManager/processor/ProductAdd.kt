package com.adrosonic.craftexchangemarketing.syncManager.processor

import android.content.Context
import android.content.Intent
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.services.ProductCreateService
import com.adrosonic.craftexchangemarketing.syncManager.processor.objects.ItemType

/**
 * Created by 'Rital Naik on 15/05/18.
 */
class ProductAdd: ChangeProcessor<ItemType> {

    override val elementsInProgress: InProgressTracker<ItemType>
        get() = InProgressTracker()

    override fun processChangedLocalElements(elements: List<ItemType>, context: Context) {
        elements.forEach { addProduct(it, context) }
    }

    override val predicateForLocallyTrackedElements: String
        get() = "${ArtisanProducts.COLUMN_ACTION_CREATE}=1"


    fun addProduct(itemType: ItemType, context: Context) {
        val work = Intent().apply { putExtra(ProductCreateService.KEY_ID, itemType.id) }
        ProductCreateService.enqueueWork(context, work)
    }

}